
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.application.ports.out.DeviceOutputPort;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.DeviceRepository;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.DeviceDocument;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper.DeviceMongoMapper;

@Component
public class DevicePersistenceAdapter implements DeviceOutputPort {

        private final DeviceRepository repository;
        private final DeviceMongoMapper mapper;
        private final MongoTemplate mongoTemplate;

        public DevicePersistenceAdapter(
                        DeviceRepository repository,
                        DeviceMongoMapper mapper,
                        MongoTemplate mongoTemplate) {

                this.repository = repository;
                this.mapper = mapper;
                this.mongoTemplate = mongoTemplate;
        }

        @Override
        public Device save(Device device) {
                return mapper.toDomain(
                                repository.save(mapper.toDocument(device)));
        }

        @Override
        public Optional<Device> findById(String id) {
                return repository.findById(id)
                                .map(mapper::toDomain);
        }

        @Override
        public Optional<Device> findByInstallationId(
                        String installationId) {

                return repository.findByInstallationId(installationId)
                                .map(mapper::toDomain);
        }

        @Override
        public Optional<Device> findByTokenHash(String tokenHash) {
                return repository.findByTokenHash(tokenHash)
                                .map(mapper::toDomain);
        }

        @Override
        public List<Device> findAll() {
                return repository.findAll()
                                .stream()
                                .map(mapper::toDomain)
                                .toList();
        }

        @Override
        public boolean approveIfPending(
                        String deviceId,
                        String clientId,
                        String otpHash,
                        Instant approvedAt,
                        Instant otpExpiresAt) {

                Query query = new Query(
                                Criteria.where("_id").is(deviceId)
                                                .and("status").is(Device.Status.PENDING.name()));

                Update update = new Update()
                                .set("clientId", clientId)
                                .set("status", Device.Status.APPROVED.name())
                                .set("approvedAt", approvedAt)
                                .set("otpHash", otpHash)
                                .set("otpExpiresAt", otpExpiresAt)
                                .set("otpAttempts", 0)
                                .unset("tokenHash");

                return mongoTemplate.updateFirst(
                                query,
                                update,
                                DeviceDocument.class).getModifiedCount() == 1;
        }

        @Override
        public boolean registerFailedOtpAttempt(
                        String deviceId,
                        String submittedOtpHash,
                        Instant now,
                        int maxAttempts) {

                Query query = new Query(
                                Criteria.where("_id").is(deviceId)
                                                .and("status").is(Device.Status.APPROVED.name())
                                                .and("otpHash").ne(submittedOtpHash)
                                                .and("otpExpiresAt").gt(now)
                                                .and("otpAttempts").lt(maxAttempts));

                Update update = new Update()
                                .inc("otpAttempts", 1);

                return mongoTemplate.updateFirst(
                                query,
                                update,
                                DeviceDocument.class).getModifiedCount() == 1;
        }

        @Override
        public boolean activateIfApproved(
                        String deviceId,
                        String otpHash,
                        Instant now,
                        int maxAttempts,
                        String tokenHash) {

                Query query = new Query(
                                Criteria.where("_id").is(deviceId)
                                                .and("status").is(Device.Status.APPROVED.name())
                                                .and("otpHash").is(otpHash)
                                                .and("otpExpiresAt").gt(now)
                                                .and("otpAttempts").lt(maxAttempts));

                Update update = new Update()
                                .set("status", Device.Status.ACTIVE.name())
                                .set("tokenHash", tokenHash)
                                .unset("otpHash")
                                .unset("otpExpiresAt")
                                .unset("otpAttempts");

                return mongoTemplate.updateFirst(
                                query,
                                update,
                                DeviceDocument.class).getModifiedCount() == 1;
        }

        @Override
        public boolean revokeIfNotRevoked(String deviceId) {

                Query query = new Query(
                                Criteria.where("_id").is(deviceId)
                                                .and("status").ne(Device.Status.REVOKED.name()));

                Update update = new Update()
                                .set("status", Device.Status.REVOKED.name())
                                .unset("tokenHash")
                                .unset("otpHash")
                                .unset("otpExpiresAt")
                                .unset("otpAttempts");

                return mongoTemplate.updateFirst(
                                query,
                                update,
                                DeviceDocument.class).getModifiedCount() == 1;
        }
}
