package com.monolith.hardware_serial_port;

import com.monolith.dsxp.DsxpException;
import com.monolith.dsxp.driver.serial.SerialConfig;
import com.monolith.dsxp.driver.serial.SerialWrapper;
import com.monolith.dsxp.util.ExceptionUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Create by Levent8421
 * Date: 2023/12/28 17:58
 * ClassName: MitDspSerialWrapper
 * Description:
 * MiT DSP Serial Port
 *
 * @author levent8421
 */
public class SerialWrapperImpl implements SerialWrapper {
    private final SerialConfig config;
    private SerialPort serialPort;

    public SerialWrapperImpl(SerialConfig config) {
        this.config = config;
    }

    @Override
    public void open() throws DsxpException {
        close();
        String fileName = config.getFileName();
        File file = new File(fileName);
        int baudRate = config.getBaudRate();
        try {
            serialPort = new SerialPort(file, baudRate, 0);
        } catch (Exception e) {
            throw new DsxpException(ExceptionUtils.getMessage(e), e);
        }
    }

    @Override
    public void close() throws DsxpException {
        if (serialPort == null) {
            return;
        }
        serialPort.close();
        serialPort = null;
    }

    @Override
    public InputStream getInputStream() {
        return serialPort == null ? null : serialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return serialPort == null ? null : serialPort.getOutputStream();
    }
}
