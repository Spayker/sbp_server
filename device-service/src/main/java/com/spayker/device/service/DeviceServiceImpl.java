package com.spayker.device.service;

import com.spayker.device.client.AccountServiceClient;
import com.spayker.device.domain.Device;
import com.spayker.device.exception.DeviceException;
import com.spayker.device.repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static java.util.Optional.ofNullable;

@Service
public class DeviceServiceImpl implements DeviceService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AccountServiceClient accountClient;

	@Autowired
	private DeviceRepository repository;

	@Override
	public Device findByDeviceId(String deviceId) {
		if (deviceId.isEmpty()){ throw new IllegalArgumentException("Device id can not be empty"); }
		return repository.findByDeviceId(deviceId);
	}

	@Override
	public Device create(Device device) {
		Device existing = repository.findByDeviceId(device.getDeviceId());
		Assert.isNull(existing, "device already exists: " + device.getDeviceId());
		repository.save(device);
		log.info("new device has been created: " + device.getDeviceId());
		return device;
	}

	@Override
	public void saveChanges(Device device) {

		String deviceId = device.getDeviceId();
		if(deviceId.isEmpty()){
			throw new DeviceException("Device with id can not be empty");
		}

		Device storedDevice = ofNullable(repository.findByDeviceId(device.getDeviceId()))
				.orElseThrow(() -> new IllegalArgumentException("Device with id " + deviceId + " does not exist"));

		storedDevice.setHrData(device.getHrData());
		storedDevice.setDate(device.getDate());
		repository.save(storedDevice);

		log.debug("device {} changes has been saved", deviceId);
	}
}
