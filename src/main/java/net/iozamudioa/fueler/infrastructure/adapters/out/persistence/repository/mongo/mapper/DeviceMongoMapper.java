
package net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.mapper;

import org.springframework.stereotype.Component;

import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.infrastructure.adapters.out.persistence.repository.mongo.document.DeviceDocument;

@Component
public class DeviceMongoMapper {

    public DeviceDocument toDocument(Device device) {
        return new DeviceDocument(
                device.id(),
                device.installationId(),
                device.label(),
                device.clientId(),
                device.status(),
                device.tokenHash(),
                device.createdAt(),
                device.approvedAt());
    }

    public Device toDomain(DeviceDocument document) {
        return new Device(
                document.id(),
                document.installationId(),
                document.label(),
                document.clientId(),
                document.status(),
                document.tokenHash(),
                document.createdAt(),
                document.approvedAt());
    }
}
