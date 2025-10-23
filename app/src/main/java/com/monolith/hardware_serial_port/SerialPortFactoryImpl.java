package com.monolith.hardware_serial_port;


import com.monolith.dsxp.driver.serial.SerialConfig;
import com.monolith.dsxp.driver.serial.SerialFactory;
import com.monolith.dsxp.driver.serial.SerialWrapper;

/**
 * Create by Levent8421
 * Date: 2023/12/28 18:00
 * ClassName: MitDspSerialPortFactory
 * Description:
 * Serial Factory
 *
 * @author levent8421
 */
public class SerialPortFactoryImpl implements SerialFactory {

    @Override
    public SerialWrapper buildSerial(SerialConfig serialConfig) {
        return new SerialWrapperImpl(serialConfig);
    }
}
