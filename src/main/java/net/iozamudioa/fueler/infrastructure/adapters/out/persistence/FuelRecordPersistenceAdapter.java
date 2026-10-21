
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.FuelRecordOutputPort;
import net.iozamudioa.fueler.domain.FuelPriceSuggestion;
import net.iozamudioa.fueler.domain.FuelRecord;
import net.iozamudioa.fueler.domain.FuelRecordDetail;
import net.iozamudioa.fueler.domain.FuelRecordExportSearch;
import net.iozamudioa.fueler.domain.FuelRecordSearch;
import net.iozamudioa.fueler.domain.Operator;
import net.iozamudioa.fueler.domain.PagedResult;
import net.iozamudioa.fueler.domain.Vehicle;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;
import net.iozamudioa.fueler.domain.exception.ResourceVersionConflictException;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.FuelRecordHistoryRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.FuelRecordRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordDocument;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordHistoryDocument;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.FuelRecordHistoryDocument.FieldChange;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.OperatorDocument;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.VehicleDocument;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.FuelRecordMongoMapper;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.OperatorMongoMapper;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.VehicleMongoMapper;

@Component
public class FuelRecordPersistenceAdapter implements FuelRecordOutputPort {

        private final FuelRecordRepository fuelRecordRepository;
        private final FuelRecordHistoryRepository historyRepository;
        private final FuelRecordMongoMapper mapper;
        private final VehicleMongoMapper vehicleMongoMapper;
        private final OperatorMongoMapper operatorMongoMapper;
        private final MongoTemplate mongoTemplate;
        private final MongoConverter mongoConverter;

        public FuelRecordPersistenceAdapter(
                        FuelRecordRepository fuelRecordRepository,
                        FuelRecordHistoryRepository historyRepository,
                        FuelRecordMongoMapper mapper,
                        MongoTemplate mongoTemplate,
                        MongoConverter mongoConverter,
                        VehicleMongoMapper vehicleMongoMapper,
                        OperatorMongoMapper operatorMongoMapper) {

                this.fuelRecordRepository = fuelRecordRepository;
                this.historyRepository = historyRepository;
                this.mapper = mapper;
                this.mongoTemplate = mongoTemplate;
                this.mongoConverter = mongoConverter;
                this.vehicleMongoMapper = vehicleMongoMapper;
                this.operatorMongoMapper = operatorMongoMapper;
        }

        @Override
        public FuelRecord save(FuelRecord fuelRecord) {
                return mapper.toDomain(
                                fuelRecordRepository.save(
                                                mapper.toDocument(fuelRecord)));
        }

        @Override
        public Optional<FuelRecord> findByClientIdAndRequestId(
                        String clientId,
                        String requestId) {

                return fuelRecordRepository
                                .findByClientIdAndRequestId(clientId, requestId)
                                .map(mapper::toDomain);
        }

        @Override
        public List<FuelRecord> findAllByClientId(String clientId) {
                return fuelRecordRepository.findAllByClientId(clientId)
                                .stream()
                                .map(mapper::toDomain)
                                .toList();
        }

        @Override
        public List<FuelRecord> findAllByClientIdAndDateBetween(
                        String clientId,
                        LocalDate startDate,
                        LocalDate endDate) {

                return fuelRecordRepository
                                .findAllByClientIdAndDateBetween(
                                                clientId,
                                                startDate,
                                                endDate)
                                .stream()
                                .map(mapper::toDomain)
                                .toList();
        }

        @Override
        public Optional<FuelRecord> findByIdAndClientId(
                        String id,
                        String clientId) {

                return fuelRecordRepository
                                .findByIdAndClientId(id, clientId)
                                .map(mapper::toDomain);
        }

        @Override
        public FuelRecord update(
                        FuelRecord record,
                        Long expectedVersion,
                        String modifiedBy,
                        String reason) {

                // 1. Obtener estado anterior.
                FuelRecord current = findByIdAndClientId(
                                record.id(),
                                record.clientId()).orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "FuelRecord", record.id()));

                // 2. Verificar versión.
                if (!Objects.equals(current.version(), expectedVersion)) {
                        throw new ResourceVersionConflictException(
                                        "Fuel record was modified by another request");
                }

                // 3. Identificar campos modificados.
                Map<String, FieldChange> changes = detectChanges(
                                current,
                                record);

                if (changes.isEmpty()) {
                        return current;
                }

                // 4. Actualización condicional.
                Query query = new Query(
                                Criteria.where("_id").is(record.id())
                                                .and("clientId").is(record.clientId())
                                                .and("version").is(expectedVersion));

                Update update = new Update()
                                .set(FuelRecordDocument::date, record.date())
                                .set(FuelRecordDocument::vehicleReference, record.vehicleReference())
                                .set(FuelRecordDocument::area, record.area())
                                .set(FuelRecordDocument::operatorReference, record.operatorReference())
                                .set(FuelRecordDocument::operatorName, record.operatorName())
                                .set(FuelRecordDocument::tankLevel, record.tankLevel())
                                .set(FuelRecordDocument::distanceKm, record.distanceKm())
                                .set(FuelRecordDocument::odometerKm, record.odometerKm())
                                .set(FuelRecordDocument::ticketNumber, record.ticketNumber())
                                .set(FuelRecordDocument::note, record.note())
                                .set(FuelRecordDocument::fuelType, record.fuelType())
                                .set(FuelRecordDocument::pricePerLiter, record.pricePerLiter())
                                .set(FuelRecordDocument::liters, record.liters())
                                .set(FuelRecordDocument::totalAmount, record.totalAmount())
                                .set(FuelRecordDocument::efficiencyKmPerLiter, record.efficiencyKmPerLiter())
                                .inc("version", 1L);

                FuelRecordDocument updated = mongoTemplate.findAndModify(
                                query,
                                update,
                                FindAndModifyOptions.options().returnNew(true),
                                FuelRecordDocument.class);

                if (updated == null) {
                        throw new ResourceVersionConflictException(
                                        "Fuel record was modified by another request");
                }

                // 5. Guardar historial de la corrección.
                FuelRecordHistoryDocument history = new FuelRecordHistoryDocument(
                                UUID.randomUUID().toString(),
                                record.clientId(),
                                record.id(),
                                updated.version(),
                                modifiedBy,
                                Instant.now(),
                                reason,
                                changes);

                historyRepository.save(history);

                // 6. Devolver registro actualizado.
                return mapper.toDomain(updated);
        }

        @Override
        public PagedResult<FuelRecordDetail> search(
                        String clientId,
                        FuelRecordSearch criteria) {

                List<AggregationOperation> operations = new ArrayList<>();

                Criteria filter = Criteria.where("clientId").is(clientId);

                if (criteria.from() != null && criteria.to() != null) {
                        filter.and("date")
                                        .gte(criteria.from())
                                        .lte(criteria.to());
                } else if (criteria.from() != null) {
                        filter.and("date").gte(criteria.from());
                } else if (criteria.to() != null) {
                        filter.and("date").lte(criteria.to());
                }

                if (criteria.vehicleReference() != null) {
                        filter.and("vehicleReference")
                                        .is(criteria.vehicleReference());
                }

                if (criteria.operatorReference() != null) {
                        filter.and("operatorReference")
                                        .is(criteria.operatorReference());
                }

                if (criteria.area() != null) {
                        filter.and("area").is(criteria.area());
                }

                operations.add(Aggregation.match(filter));

                operations.add(Aggregation.sort(
                                Sort.by(
                                                Sort.Order.desc("date"),
                                                Sort.Order.desc("_id"))));

                operations.add(context -> {

                        List<Document> dataPipeline = new ArrayList<>();

                        dataPipeline.add(new Document("$skip",
                                        (long) criteria.page() * criteria.size()));

                        dataPipeline.add(new Document("$limit", criteria.size()));

                        dataPipeline.addAll(
                                        lookup("vehicles", "vehicleReference", "vehicle")
                                                        .toPipelineStages(context));

                        dataPipeline.addAll(
                                        lookup("operators", "operatorReference", "operator")
                                                        .toPipelineStages(context));

                        dataPipeline.addAll(
                                        unwind("vehicle").toPipelineStages(context));

                        dataPipeline.addAll(
                                        unwind("operator").toPipelineStages(context));

                        return new Document("$facet",
                                        new Document()
                                                        .append("metadata", List.of(
                                                                        new Document("$count", "total")))
                                                        .append("data", dataPipeline));
                });

                Aggregation aggregation = Aggregation.newAggregation(
                                FuelRecordDocument.class,
                                operations);

                AggregationResults<Document> results = mongoTemplate.aggregate(
                                aggregation,
                                "fuel_records",
                                Document.class);

                Document result = results.getUniqueMappedResult();

                if (result == null) {
                        return PagedResult.of(
                                        List.of(),
                                        criteria.page(),
                                        criteria.size(),
                                        0);
                }

                List<Document> metadata = result.getList("metadata", Document.class);

                long total = metadata.isEmpty()
                                ? 0
                                : ((Number) metadata.get(0).get("total")).longValue();

                List<Document> data = result.getList("data", Document.class);

                List<FuelRecordDetail> content = new ArrayList<>();

                for (Document row : data) {

                        // Los tres mappers existentes convierten documentos
                        // de persistencia a modelos del dominio.
                        //
                        // La conversión BSON -> Document tipado se completa
                        // cuando confirmemos los campos de tus @Document.

                        FuelRecord fuelRecord = mapFuelRecord(row);

                        Vehicle vehicle = row.get("vehicle") == null
                                        ? null
                                        : mapVehicle((Document) row.get("vehicle"));

                        Operator operator = row.get("operator") == null
                                        ? null
                                        : mapOperator((Document) row.get("operator"));

                        content.add(new FuelRecordDetail(
                                        fuelRecord,
                                        vehicle,
                                        operator));
                }

                return PagedResult.of(
                                content,
                                criteria.page(),
                                criteria.size(),
                                total);
        }

        @Override
        public Optional<FuelPriceSuggestion> findLatestPrice(
                        String clientId,
                        String gasStationReference,
                        String fuelType,
                        LocalDate date) {

                Query query = new Query();

                query.addCriteria(
                                Criteria.where("clientId").is(clientId)
                                                .and("gasStationReference").is(gasStationReference)
                                                .and("fuelType").is(fuelType)
                                                .and("date").lte(date)
                                                .and("pricePerLiter").gt(0));

                query.with(
                                Sort.by(
                                                Sort.Order.desc("date"),
                                                Sort.Order.desc("createdAt"),
                                                Sort.Order.desc("_id")));

                query.limit(1);

                FuelRecordDocument document = mongoTemplate.findOne(
                                query,
                                FuelRecordDocument.class);

                if (document == null) {
                        return Optional.empty();
                }

                return Optional.of(
                                new FuelPriceSuggestion(
                                                document.gasStationReference(),
                                                document.fuelType(),
                                                document.pricePerLiter(),
                                                document.date()));
        }

        @Override
        public List<FuelRecordDetail> export(
                        String clientId,
                        FuelRecordExportSearch criteria) {

                Criteria filter = Criteria.where("clientId").is(clientId)
                                .and("date")
                                .gte(criteria.from())
                                .lte(criteria.to());

                if (criteria.vehicleReference() != null
                                && !criteria.vehicleReference().isBlank()) {
                        filter.and("vehicleReference")
                                        .is(criteria.vehicleReference().trim().toUpperCase());
                }

                if (criteria.operatorReference() != null
                                && !criteria.operatorReference().isBlank()) {
                        filter.and("operatorReference")
                                        .is(criteria.operatorReference().trim());
                }

                if (criteria.area() != null
                                && !criteria.area().isBlank()) {
                        filter.and("area")
                                        .is(criteria.area().trim());
                }

                List<AggregationOperation> operations = new ArrayList<>();

                operations.add(Aggregation.match(filter));

                operations.add(Aggregation.sort(
                                Sort.by(
                                                Sort.Order.desc("date"),
                                                Sort.Order.desc("_id"))));

                operations.add(
                                lookup("vehicles", "vehicleReference", "vehicle"));

                operations.add(
                                lookup("operators", "operatorReference", "operator"));

                operations.add(unwind("vehicle"));
                operations.add(unwind("operator"));

                Aggregation aggregation = Aggregation.newAggregation(
                                FuelRecordDocument.class,
                                operations);

                AggregationResults<Document> results = mongoTemplate.aggregate(
                                aggregation,
                                "fuel_records",
                                Document.class);

                List<FuelRecordDetail> records = new ArrayList<>();

                for (Document row : results.getMappedResults()) {

                        FuelRecord fuelRecord = mapFuelRecord(row);

                        Vehicle vehicle = row.get("vehicle") == null
                                        ? null
                                        : mapVehicle((Document) row.get("vehicle"));

                        Operator operator = row.get("operator") == null
                                        ? null
                                        : mapOperator((Document) row.get("operator"));

                        records.add(new FuelRecordDetail(
                                        fuelRecord,
                                        vehicle,
                                        operator));
                }

                return records;
        }

        private AggregationOperation lookup(
                        String collection,
                        String referenceField,
                        String alias) {

                return context -> new Document("$lookup",
                                new Document("from", collection)
                                                .append("let", new Document()
                                                                .append("tenant", "$clientId")
                                                                .append("reference", "$" + referenceField))
                                                .append("pipeline", List.of(
                                                                new Document("$match",
                                                                                new Document("$expr",
                                                                                                new Document("$and",
                                                                                                                List.of(
                                                                                                                                new Document("$eq",
                                                                                                                                                List.of(
                                                                                                                                                                "$clientId",
                                                                                                                                                                "$$tenant")),
                                                                                                                                new Document("$eq",
                                                                                                                                                List.of(
                                                                                                                                                                "$reference",
                                                                                                                                                                "$$reference"))))))))
                                                .append("as", alias));
        }

        private AggregationOperation unwind(String field) {
                return context -> new Document("$unwind",
                                new Document("path", "$" + field)
                                                .append("preserveNullAndEmptyArrays", true));
        }

        private Map<String, FieldChange> detectChanges(
                        FuelRecord before,
                        FuelRecord after) {

                Map<String, FieldChange> changes = new LinkedHashMap<>();

                addChange(changes, "date",
                                before.date(), after.date());

                addChange(changes, "vehicleReference",
                                before.vehicleReference(), after.vehicleReference());

                addChange(changes, "area",
                                before.area(), after.area());

                addChange(changes, "operatorReference",
                                before.operatorReference(), after.operatorReference());

                addChange(changes, "operatorName",
                                before.operatorName(), after.operatorName());

                addChange(changes, "tankLevel",
                                before.tankLevel(), after.tankLevel());

                addChange(changes, "distanceKm",
                                before.distanceKm(), after.distanceKm());

                addChange(changes, "odometerKm",
                                before.odometerKm(), after.odometerKm());

                addChange(changes, "ticketNumber",
                                before.ticketNumber(), after.ticketNumber());

                addChange(changes, "note",
                                before.note(), after.note());

                addChange(changes, "fuelType",
                                before.fuelType(), after.fuelType());

                addChange(changes, "pricePerLiter",
                                before.pricePerLiter(), after.pricePerLiter());

                addChange(changes, "liters",
                                before.liters(), after.liters());

                addChange(changes, "totalAmount",
                                before.totalAmount(), after.totalAmount());

                addChange(changes, "efficiencyKmPerLiter",
                                before.efficiencyKmPerLiter(),
                                after.efficiencyKmPerLiter());

                return changes;
        }

        private void addChange(
                        Map<String, FieldChange> changes,
                        String field,
                        Object before,
                        Object after) {

                if (!Objects.equals(before, after)) {
                        changes.put(field, new FieldChange(before, after));
                }
        }

        private FuelRecord mapFuelRecord(Document document) {
                FuelRecordDocument entity = mongoConverter.read(
                                FuelRecordDocument.class, document);

                return mapper.toDomain(entity);
        }

        private Vehicle mapVehicle(Document document) {
                VehicleDocument entity = mongoConverter.read(
                                VehicleDocument.class, document);

                return vehicleMongoMapper.toDomain(entity);
        }

        private Operator mapOperator(Document document) {
                OperatorDocument entity = mongoConverter.read(
                                OperatorDocument.class, document);

                return operatorMongoMapper.toDomain(entity);
        }

}
