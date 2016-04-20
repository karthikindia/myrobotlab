package org.myrobotlab.service;

import org.myrobotlab.framework.Service;
import org.myrobotlab.framework.repo.ServiceType;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.interfaces.I2CControl;
import org.slf4j.Logger;

/**
 * 
 * MPU6050 - MPU-6050 sensor contains a MEMS accelerometer and a MEMS gyro in a single chip. 
 * It is very accurate, as it contains 16-bits analog to digital conversion hardware for each channel. 
 * Therefore it captures the x, y, and z channel at the same time.
 * http://playground.arduino.cc/Main/MPU-6050
 *
 */
public class Mpu6050 extends Service{

	private static final long serialVersionUID = 1L;

	public final static Logger log = LoggerFactory.getLogger(Mpu6050.class);

	StringBuilder debugRX = new StringBuilder();

	transient I2CControl controller;
	
    // Default i2cAddress
	public int busAddress = 1;
	public int deviceAddress = 0x68;
	public String type = "MPU-6050";

	public static final int MPU6050_ADDRESS_AD0_LOW  =  0x68; // address pin low (GND), default for InvenSense evaluation board
	public static final int MPU6050_ADDRESS_AD0_HIGH =  0x69; // address pin high (VCC)
	public static final int MPU6050_DEFAULT_ADDRESS  =  MPU6050_ADDRESS_AD0_LOW;

	public static final int MPU6050_RA_XG_OFFS_TC     =  0x00; //[7] PWR_MODE, [6:1] XG_OFFS_TC, [0] OTP_BNK_VLD
	public static final int MPU6050_RA_YG_OFFS_TC     =  0x01; //[7] PWR_MODE, [6:1] YG_OFFS_TC, [0] OTP_BNK_VLD
	public static final int MPU6050_RA_ZG_OFFS_TC     =  0x02; //[7] PWR_MODE, [6:1] ZG_OFFS_TC, [0] OTP_BNK_VLD
	public static final int MPU6050_RA_X_FINE_GAIN    =  0x03; //[7:0] X_FINE_GAIN
	public static final int MPU6050_RA_Y_FINE_GAIN    =  0x04; //[7:0] Y_FINE_GAIN
	public static final int MPU6050_RA_Z_FINE_GAIN    =  0x05; //[7:0] Z_FINE_GAIN
	public static final int MPU6050_RA_XA_OFFS_H      =  0x06; //[15:0] XA_OFFS
	public static final int MPU6050_RA_XA_OFFS_L_TC   =  0x07;
	public static final int MPU6050_RA_YA_OFFS_H      =  0x08; //[15:0] YA_OFFS
	public static final int MPU6050_RA_YA_OFFS_L_TC   =  0x09;
	public static final int MPU6050_RA_ZA_OFFS_H      =  0x0A; //[15:0] ZA_OFFS
	public static final int MPU6050_RA_ZA_OFFS_L_TC   =  0x0B;
	public static final int MPU6050_RA_SELF_TEST_X    =  0x0D; //[7:5] XA_TEST[4-2], [4:0] XG_TEST[4-0]
	public static final int MPU6050_RA_SELF_TEST_Y    =  0x0E; //[7:5] YA_TEST[4-2], [4:0] YG_TEST[4-0]
	public static final int MPU6050_RA_SELF_TEST_Z    =  0x0F; //[7:5] ZA_TEST[4-2], [4:0] ZG_TEST[4-0]
	public static final int MPU6050_RA_SELF_TEST_A    =  0x10; //[5:4] XA_TEST[1-0], [3:2] YA_TEST[1-0], [1:0] ZA_TEST[1-0]
	public static final int MPU6050_RA_XG_OFFS_USRH   =  0x13; //[15:0] XG_OFFS_USR
	public static final int MPU6050_RA_XG_OFFS_USRL   =  0x14;
	public static final int MPU6050_RA_YG_OFFS_USRH   =  0x15; //[15:0] YG_OFFS_USR
	public static final int MPU6050_RA_YG_OFFS_USRL   =  0x16;
	public static final int MPU6050_RA_ZG_OFFS_USRH   =  0x17; //[15:0] ZG_OFFS_USR
	public static final int MPU6050_RA_ZG_OFFS_USRL   =  0x18;
	public static final int MPU6050_RA_SMPLRT_DIV     =  0x19;
	public static final int MPU6050_RA_CONFIG         =  0x1A;
	public static final int MPU6050_RA_GYRO_CONFIG    =  0x1B;
	public static final int MPU6050_RA_ACCEL_CONFIG   =  0x1C;
	public static final int MPU6050_RA_FF_THR         =  0x1D;
	public static final int MPU6050_RA_FF_DUR         =  0x1E;
	public static final int MPU6050_RA_MOT_THR        =  0x1F;
	public static final int MPU6050_RA_MOT_DUR        =  0x20;
	public static final int MPU6050_RA_ZRMOT_THR      =  0x21;
	public static final int MPU6050_RA_ZRMOT_DUR      =  0x22;
	public static final int MPU6050_RA_FIFO_EN        =  0x23;
	public static final int MPU6050_RA_I2C_MST_CTRL   =  0x24;
	public static final int MPU6050_RA_I2C_SLV0_ADDR  =  0x25;
	public static final int MPU6050_RA_I2C_SLV0_REG   =  0x26;
	public static final int MPU6050_RA_I2C_SLV0_CTRL  =  0x27;
	public static final int MPU6050_RA_I2C_SLV1_ADDR  =  0x28;
	public static final int MPU6050_RA_I2C_SLV1_REG   =  0x29;
	public static final int MPU6050_RA_I2C_SLV1_CTRL  =  0x2A;
	public static final int MPU6050_RA_I2C_SLV2_ADDR  =  0x2B;
	public static final int MPU6050_RA_I2C_SLV2_REG   =  0x2C;
	public static final int MPU6050_RA_I2C_SLV2_CTRL  =  0x2D;
	public static final int MPU6050_RA_I2C_SLV3_ADDR  =  0x2E;
	public static final int MPU6050_RA_I2C_SLV3_REG   =  0x2F;
	public static final int MPU6050_RA_I2C_SLV3_CTRL  =  0x30;
	public static final int MPU6050_RA_I2C_SLV4_ADDR  =  0x31;
	public static final int MPU6050_RA_I2C_SLV4_REG   =  0x32;
	public static final int MPU6050_RA_I2C_SLV4_DO    =  0x33;
	public static final int MPU6050_RA_I2C_SLV4_CTRL  =  0x34;
	public static final int MPU6050_RA_I2C_SLV4_DI    =  0x35;
	public static final int MPU6050_RA_I2C_MST_STATUS =  0x36;
	public static final int MPU6050_RA_INT_PIN_CFG    =  0x37;
	public static final int MPU6050_RA_INT_ENABLE     =  0x38;
	public static final int MPU6050_RA_DMP_INT_STATUS =  0x39;
	public static final int MPU6050_RA_INT_STATUS     =  0x3A;
	public static final int MPU6050_RA_ACCEL_XOUT_H   =  0x3B;
	public static final int MPU6050_RA_ACCEL_XOUT_L   =  0x3C;
	public static final int MPU6050_RA_ACCEL_YOUT_H   =  0x3D;
	public static final int MPU6050_RA_ACCEL_YOUT_L   =  0x3E;
	public static final int MPU6050_RA_ACCEL_ZOUT_H   =  0x3F;
	public static final int MPU6050_RA_ACCEL_ZOUT_L   =  0x40;
	public static final int MPU6050_RA_TEMP_OUT_H     =  0x41;
	public static final int MPU6050_RA_TEMP_OUT_L     =  0x42;
	public static final int MPU6050_RA_GYRO_XOUT_H    =  0x43;
	public static final int MPU6050_RA_GYRO_XOUT_L    =  0x44;
	public static final int MPU6050_RA_GYRO_YOUT_H    =  0x45;
	public static final int MPU6050_RA_GYRO_YOUT_L    =  0x46;
	public static final int MPU6050_RA_GYRO_ZOUT_H    =  0x47;
	public static final int MPU6050_RA_GYRO_ZOUT_L    =  0x48;
	public static final int MPU6050_RA_EXT_SENS_DATA_00 = 0x49;
	public static final int MPU6050_RA_EXT_SENS_DATA_01 = 0x4A;
	public static final int MPU6050_RA_EXT_SENS_DATA_02 = 0x4B;
	public static final int MPU6050_RA_EXT_SENS_DATA_03 = 0x4C;
	public static final int MPU6050_RA_EXT_SENS_DATA_04 = 0x4D;
	public static final int MPU6050_RA_EXT_SENS_DATA_05 = 0x4E;
	public static final int MPU6050_RA_EXT_SENS_DATA_06 = 0x4F;
	public static final int MPU6050_RA_EXT_SENS_DATA_07 = 0x50;
	public static final int MPU6050_RA_EXT_SENS_DATA_08 = 0x51;
	public static final int MPU6050_RA_EXT_SENS_DATA_09 = 0x52;
	public static final int MPU6050_RA_EXT_SENS_DATA_10 = 0x53;
	public static final int MPU6050_RA_EXT_SENS_DATA_11 = 0x54;
	public static final int MPU6050_RA_EXT_SENS_DATA_12 = 0x55;
	public static final int MPU6050_RA_EXT_SENS_DATA_13 = 0x56;
	public static final int MPU6050_RA_EXT_SENS_DATA_14 = 0x57;
	public static final int MPU6050_RA_EXT_SENS_DATA_15 = 0x58;
	public static final int MPU6050_RA_EXT_SENS_DATA_16 = 0x59;
	public static final int MPU6050_RA_EXT_SENS_DATA_17 = 0x5A;
	public static final int MPU6050_RA_EXT_SENS_DATA_18 = 0x5B;
	public static final int MPU6050_RA_EXT_SENS_DATA_19 = 0x5C;
	public static final int MPU6050_RA_EXT_SENS_DATA_20 = 0x5D;
	public static final int MPU6050_RA_EXT_SENS_DATA_21 = 0x5E;
	public static final int MPU6050_RA_EXT_SENS_DATA_22 = 0x5F;
	public static final int MPU6050_RA_EXT_SENS_DATA_23 = 0x60;
	public static final int MPU6050_RA_MOT_DETECT_STATUS    = 0x61;
	public static final int MPU6050_RA_I2C_SLV0_DO      = 0x63;
	public static final int MPU6050_RA_I2C_SLV1_DO      = 0x64;
	public static final int MPU6050_RA_I2C_SLV2_DO      = 0x65;
	public static final int MPU6050_RA_I2C_SLV3_DO      = 0x66;
	public static final int MPU6050_RA_I2C_MST_DELAY_CTRL   = 0x67;
	public static final int MPU6050_RA_SIGNAL_PATH_RESET    = 0x68;
	public static final int MPU6050_RA_MOT_DETECT_CTRL      = 0x69;
	public static final int MPU6050_RA_USER_CTRL        = 0x6A;
	public static final int MPU6050_RA_PWR_MGMT_1       = 0x6B;
	public static final int MPU6050_RA_PWR_MGMT_2       = 0x6C;
	public static final int MPU6050_RA_BANK_SEL         = 0x6D;
	public static final int MPU6050_RA_MEM_START_ADDR   = 0x6E;
	public static final int MPU6050_RA_MEM_R_W          = 0x6F;
	public static final int MPU6050_RA_DMP_CFG_1        = 0x70;
	public static final int MPU6050_RA_DMP_CFG_2        = 0x71;
	public static final int MPU6050_RA_FIFO_COUNTH      = 0x72;
	public static final int MPU6050_RA_FIFO_COUNTL      = 0x73;
	public static final int MPU6050_RA_FIFO_R_W         = 0x74;
	public static final int MPU6050_RA_WHO_AM_I         = 0x75;

	public static final int MPU6050_SELF_TEST_XA_1_BIT     = 0x07;
	public static final int MPU6050_SELF_TEST_XA_1_LENGTH  = 0x03;
	public static final int MPU6050_SELF_TEST_XA_2_BIT     = 0x05;
	public static final int MPU6050_SELF_TEST_XA_2_LENGTH  = 0x02;
	public static final int MPU6050_SELF_TEST_YA_1_BIT     = 0x07;
	public static final int MPU6050_SELF_TEST_YA_1_LENGTH  = 0x03;
	public static final int MPU6050_SELF_TEST_YA_2_BIT     = 0x03;
	public static final int MPU6050_SELF_TEST_YA_2_LENGTH  = 0x02;
	public static final int MPU6050_SELF_TEST_ZA_1_BIT     = 0x07;
	public static final int MPU6050_SELF_TEST_ZA_1_LENGTH  = 0x03;
	public static final int MPU6050_SELF_TEST_ZA_2_BIT     = 0x01;
	public static final int MPU6050_SELF_TEST_ZA_2_LENGTH  = 0x02;

	public static final int MPU6050_SELF_TEST_XG_1_BIT     = 0x04;
	public static final int MPU6050_SELF_TEST_XG_1_LENGTH  = 0x05;
	public static final int MPU6050_SELF_TEST_YG_1_BIT     = 0x04;
	public static final int MPU6050_SELF_TEST_YG_1_LENGTH  = 0x05;
	public static final int MPU6050_SELF_TEST_ZG_1_BIT     = 0x04;
	public static final int MPU6050_SELF_TEST_ZG_1_LENGTH  = 0x05;

	public static final int MPU6050_TC_PWR_MODE_BIT    = 7;
	public static final int MPU6050_TC_OFFSET_BIT      = 6;
	public static final int MPU6050_TC_OFFSET_LENGTH   = 6;
	public static final int MPU6050_TC_OTP_BNK_VLD_BIT  = 0;

	public static final int MPU6050_VDDIO_LEVEL_VLOGIC  = 0;
	public static final int MPU6050_VDDIO_LEVEL_VDD     = 1;

	public static final int MPU6050_CFG_EXT_SYNC_SET_BIT    = 5;
	public static final int MPU6050_CFG_EXT_SYNC_SET_LENGTH = 3;
	public static final int MPU6050_CFG_DLPF_CFG_BIT    = 2;
	public static final int MPU6050_CFG_DLPF_CFG_LENGTH = 3;

	public static final int MPU6050_EXT_SYNC_DISABLED       = 0x0;
	public static final int MPU6050_EXT_SYNC_TEMP_OUT_L     = 0x1;
	public static final int MPU6050_EXT_SYNC_GYRO_XOUT_L    = 0x2;
	public static final int MPU6050_EXT_SYNC_GYRO_YOUT_L    = 0x3;
	public static final int MPU6050_EXT_SYNC_GYRO_ZOUT_L    = 0x4;
	public static final int MPU6050_EXT_SYNC_ACCEL_XOUT_L   = 0x5;
	public static final int MPU6050_EXT_SYNC_ACCEL_YOUT_L   = 0x6;
	public static final int MPU6050_EXT_SYNC_ACCEL_ZOUT_L   = 0x7;

	public static final int MPU6050_DLPF_BW_256         = 0x00;
	public static final int MPU6050_DLPF_BW_188         = 0x01;
	public static final int MPU6050_DLPF_BW_98          = 0x02;
	public static final int MPU6050_DLPF_BW_42          = 0x03;
	public static final int MPU6050_DLPF_BW_20          = 0x04;
	public static final int MPU6050_DLPF_BW_10          = 0x05;
	public static final int MPU6050_DLPF_BW_5           = 0x06;

	public static final int MPU6050_GCONFIG_FS_SEL_BIT      = 4;
	public static final int MPU6050_GCONFIG_FS_SEL_LENGTH   = 2;

	public static final int MPU6050_GYRO_FS_250         = 0x00;
	public static final int MPU6050_GYRO_FS_500         = 0x01;
	public static final int MPU6050_GYRO_FS_1000        = 0x02;
	public static final int MPU6050_GYRO_FS_2000        = 0x03;

	public static final int MPU6050_ACONFIG_XA_ST_BIT           = 7;
	public static final int MPU6050_ACONFIG_YA_ST_BIT           = 6;
	public static final int MPU6050_ACONFIG_ZA_ST_BIT           = 5;
	public static final int MPU6050_ACONFIG_AFS_SEL_BIT         = 4;
	public static final int MPU6050_ACONFIG_AFS_SEL_LENGTH      = 2;
	public static final int MPU6050_ACONFIG_ACCEL_HPF_BIT       = 2;
	public static final int MPU6050_ACONFIG_ACCEL_HPF_LENGTH    = 3;

	public static final int MPU6050_ACCEL_FS_2          = 0x00;
	public static final int MPU6050_ACCEL_FS_4          = 0x01;
	public static final int MPU6050_ACCEL_FS_8          = 0x02;
	public static final int MPU6050_ACCEL_FS_16         = 0x03;

	public static final int MPU6050_DHPF_RESET          = 0x00;
	public static final int MPU6050_DHPF_5              = 0x01;
	public static final int MPU6050_DHPF_2P5            = 0x02;
	public static final int MPU6050_DHPF_1P25           = 0x03;
	public static final int MPU6050_DHPF_0P63           = 0x04;
	public static final int MPU6050_DHPF_HOLD           = 0x07;

	public static final int MPU6050_TEMP_FIFO_EN_BIT    = 7;
	public static final int MPU6050_XG_FIFO_EN_BIT      = 6;
	public static final int MPU6050_YG_FIFO_EN_BIT      = 5;
	public static final int MPU6050_ZG_FIFO_EN_BIT      = 4;
	public static final int MPU6050_ACCEL_FIFO_EN_BIT   = 3;
	public static final int MPU6050_SLV2_FIFO_EN_BIT    = 2;
	public static final int MPU6050_SLV1_FIFO_EN_BIT    = 1;
	public static final int MPU6050_SLV0_FIFO_EN_BIT    = 0;

	public static final int MPU6050_MULT_MST_EN_BIT     = 7;
	public static final int MPU6050_WAIT_FOR_ES_BIT     = 6;
	public static final int MPU6050_SLV_3_FIFO_EN_BIT   = 5;
	public static final int MPU6050_I2C_MST_P_NSR_BIT   = 4;
	public static final int MPU6050_I2C_MST_CLK_BIT     = 3;
	public static final int MPU6050_I2C_MST_CLK_LENGTH  = 4;

	public static final int MPU6050_CLOCK_DIV_348       = 0x0;
	public static final int MPU6050_CLOCK_DIV_333       = 0x1;
	public static final int MPU6050_CLOCK_DIV_320       = 0x2;
	public static final int MPU6050_CLOCK_DIV_308       = 0x3;
	public static final int MPU6050_CLOCK_DIV_296       = 0x4;
	public static final int MPU6050_CLOCK_DIV_286       = 0x5;
	public static final int MPU6050_CLOCK_DIV_276       = 0x6;
	public static final int MPU6050_CLOCK_DIV_267       = 0x7;
	public static final int MPU6050_CLOCK_DIV_258       = 0x8;
	public static final int MPU6050_CLOCK_DIV_500       = 0x9;
	public static final int MPU6050_CLOCK_DIV_471       = 0xA;
	public static final int MPU6050_CLOCK_DIV_444       = 0xB;
	public static final int MPU6050_CLOCK_DIV_421       = 0xC;
	public static final int MPU6050_CLOCK_DIV_400       = 0xD;
	public static final int MPU6050_CLOCK_DIV_381       = 0xE;
	public static final int MPU6050_CLOCK_DIV_364       = 0xF;

	public static final int MPU6050_I2C_SLV_RW_BIT      = 7;
	public static final int MPU6050_I2C_SLV_ADDR_BIT    = 6;
	public static final int MPU6050_I2C_SLV_ADDR_LENGTH = 7;
	public static final int MPU6050_I2C_SLV_EN_BIT      = 7;
	public static final int MPU6050_I2C_SLV_BYTE_SW_BIT = 6;
	public static final int MPU6050_I2C_SLV_REG_DIS_BIT = 5;
	public static final int MPU6050_I2C_SLV_GRP_BIT     = 4;
	public static final int MPU6050_I2C_SLV_LEN_BIT     = 3;
	public static final int MPU6050_I2C_SLV_LEN_LENGTH  = 4;

	public static final int MPU6050_I2C_SLV4_RW_BIT         = 7;
	public static final int MPU6050_I2C_SLV4_ADDR_BIT       = 6;
	public static final int MPU6050_I2C_SLV4_ADDR_LENGTH    = 7;
	public static final int MPU6050_I2C_SLV4_EN_BIT         = 7;
	public static final int MPU6050_I2C_SLV4_INT_EN_BIT     = 6;
	public static final int MPU6050_I2C_SLV4_REG_DIS_BIT    = 5;
	public static final int MPU6050_I2C_SLV4_MST_DLY_BIT    = 4;
	public static final int MPU6050_I2C_SLV4_MST_DLY_LENGTH = 5;

	public static final int MPU6050_MST_PASS_THROUGH_BIT    = 7;
	public static final int MPU6050_MST_I2C_SLV4_DONE_BIT   = 6;
	public static final int MPU6050_MST_I2C_LOST_ARB_BIT    = 5;
	public static final int MPU6050_MST_I2C_SLV4_NACK_BIT   = 4;
	public static final int MPU6050_MST_I2C_SLV3_NACK_BIT   = 3;
	public static final int MPU6050_MST_I2C_SLV2_NACK_BIT   = 2;
	public static final int MPU6050_MST_I2C_SLV1_NACK_BIT   = 1;
	public static final int MPU6050_MST_I2C_SLV0_NACK_BIT   = 0;

	public static final int MPU6050_INTCFG_INT_LEVEL_BIT        = 7;
	public static final int MPU6050_INTCFG_INT_OPEN_BIT         = 6;
	public static final int MPU6050_INTCFG_LATCH_INT_EN_BIT     = 5;
	public static final int MPU6050_INTCFG_INT_RD_CLEAR_BIT     = 4;
	public static final int MPU6050_INTCFG_FSYNC_INT_LEVEL_BIT  = 3;
	public static final int MPU6050_INTCFG_FSYNC_INT_EN_BIT     = 2;
	public static final int MPU6050_INTCFG_I2C_BYPASS_EN_BIT    = 1;
	public static final int MPU6050_INTCFG_CLKOUT_EN_BIT        = 0;

	public static final int MPU6050_INTMODE_ACTIVEHIGH  = 0x00;
	public static final int MPU6050_INTMODE_ACTIVELOW   = 0x01;

	public static final int MPU6050_INTDRV_PUSHPULL     = 0x00;
	public static final int MPU6050_INTDRV_OPENDRAIN    = 0x01;

	public static final int MPU6050_INTLATCH_50USPULSE  = 0x00;
	public static final int MPU6050_INTLATCH_WAITCLEAR  = 0x01;

	public static final int MPU6050_INTCLEAR_STATUSREAD = 0x00;
	public static final int MPU6050_INTCLEAR_ANYREAD    = 0x01;

	public static final int MPU6050_INTERRUPT_FF_BIT            = 7;
	public static final int MPU6050_INTERRUPT_MOT_BIT           = 6;
	public static final int MPU6050_INTERRUPT_ZMOT_BIT          = 5;
	public static final int MPU6050_INTERRUPT_FIFO_OFLOW_BIT    = 4;
	public static final int MPU6050_INTERRUPT_I2C_MST_INT_BIT   = 3;
	public static final int MPU6050_INTERRUPT_PLL_RDY_INT_BIT   = 2;
	public static final int MPU6050_INTERRUPT_DMP_INT_BIT       = 1;
	public static final int MPU6050_INTERRUPT_DATA_RDY_BIT      = 0;

	// TODO: figure out what these actually do
	// UMPL source code is not very obivous
	public static final int MPU6050_DMPINT_5_BIT         =  5;
	public static final int MPU6050_DMPINT_4_BIT         =  4;
	public static final int MPU6050_DMPINT_3_BIT         =  3;
	public static final int MPU6050_DMPINT_2_BIT         =  2;
	public static final int MPU6050_DMPINT_1_BIT         =  1;
	public static final int MPU6050_DMPINT_0_BIT         =  0;

	public static final int MPU6050_MOTION_MOT_XNEG_BIT   = 7;
	public static final int MPU6050_MOTION_MOT_XPOS_BIT   = 6;
	public static final int MPU6050_MOTION_MOT_YNEG_BIT   = 5;
	public static final int MPU6050_MOTION_MOT_YPOS_BIT   = 4;
	public static final int MPU6050_MOTION_MOT_ZNEG_BIT   = 3;
	public static final int MPU6050_MOTION_MOT_ZPOS_BIT   = 2;
	public static final int MPU6050_MOTION_MOT_ZRMOT_BIT  = 0;

	public static final int MPU6050_DELAYCTRL_DELAY_ES_SHADOW_BIT = 7;
	public static final int MPU6050_DELAYCTRL_I2C_SLV4_DLY_EN_BIT = 4;
	public static final int MPU6050_DELAYCTRL_I2C_SLV3_DLY_EN_BIT = 3;
	public static final int MPU6050_DELAYCTRL_I2C_SLV2_DLY_EN_BIT = 2;
	public static final int MPU6050_DELAYCTRL_I2C_SLV1_DLY_EN_BIT = 1;
	public static final int MPU6050_DELAYCTRL_I2C_SLV0_DLY_EN_BIT = 0;

	public static final int MPU6050_PATHRESET_GYRO_RESET_BIT  = 2;
	public static final int MPU6050_PATHRESET_ACCEL_RESET_BIT = 1;
	public static final int MPU6050_PATHRESET_TEMP_RESET_BIT  = 0;

	public static final int MPU6050_DETECT_ACCEL_ON_DELAY_BIT     = 5;
	public static final int MPU6050_DETECT_ACCEL_ON_DELAY_LENGTH  = 2;
	public static final int MPU6050_DETECT_FF_COUNT_BIT           = 3;
	public static final int MPU6050_DETECT_FF_COUNT_LENGTH        = 2;
	public static final int MPU6050_DETECT_MOT_COUNT_BIT          = 1;
	public static final int MPU6050_DETECT_MOT_COUNT_LENGTH       = 2;

	public static final int MPU6050_DETECT_DECREMENT_RESET  = 0x0;
	public static final int MPU6050_DETECT_DECREMENT_1      = 0x1;
	public static final int MPU6050_DETECT_DECREMENT_2      = 0x2;
	public static final int MPU6050_DETECT_DECREMENT_4      = 0x3;

	public static final int MPU6050_USERCTRL_DMP_EN_BIT           = 7;
	public static final int MPU6050_USERCTRL_FIFO_EN_BIT          = 6;
	public static final int MPU6050_USERCTRL_I2C_MST_EN_BIT       = 5;
	public static final int MPU6050_USERCTRL_I2C_IF_DIS_BIT       = 4;
	public static final int MPU6050_USERCTRL_DMP_RESET_BIT        = 3;
	public static final int MPU6050_USERCTRL_FIFO_RESET_BIT       = 2;
	public static final int MPU6050_USERCTRL_I2C_MST_RESET_BIT    = 1;
	public static final int MPU6050_USERCTRL_SIG_COND_RESET_BIT   = 0;

	public static final int MPU6050_PWR1_DEVICE_RESET_BIT = 7;
	public static final int MPU6050_PWR1_SLEEP_BIT        = 6;
	public static final int MPU6050_PWR1_CYCLE_BIT        = 5;
	public static final int MPU6050_PWR1_TEMP_DIS_BIT     = 3;
	public static final int MPU6050_PWR1_CLKSEL_BIT       = 2;
	public static final int MPU6050_PWR1_CLKSEL_LENGTH    = 3;

	public static final int MPU6050_CLOCK_INTERNAL          = 0x00;
	public static final int MPU6050_CLOCK_PLL_XGYRO         = 0x01;
	public static final int MPU6050_CLOCK_PLL_YGYRO         = 0x02;
	public static final int MPU6050_CLOCK_PLL_ZGYRO         = 0x03;
	public static final int MPU6050_CLOCK_PLL_EXT32K        = 0x04;
	public static final int MPU6050_CLOCK_PLL_EXT19M        = 0x05;
	public static final int MPU6050_CLOCK_KEEP_RESET        = 0x07;

	public static final int MPU6050_PWR2_LP_WAKE_CTRL_BIT     = 7;
	public static final int MPU6050_PWR2_LP_WAKE_CTRL_LENGTH  = 2;
	public static final int MPU6050_PWR2_STBY_XA_BIT          = 5;
	public static final int MPU6050_PWR2_STBY_YA_BIT          = 4;
	public static final int MPU6050_PWR2_STBY_ZA_BIT          = 3;
	public static final int MPU6050_PWR2_STBY_XG_BIT          = 2;
	public static final int MPU6050_PWR2_STBY_YG_BIT          = 1;
	public static final int MPU6050_PWR2_STBY_ZG_BIT          = 0;

	public static final int MPU6050_WAKE_FREQ_1P25      = 0x0;
	public static final int MPU6050_WAKE_FREQ_2P5       = 0x1;
	public static final int MPU6050_WAKE_FREQ_5         = 0x2;
	public static final int MPU6050_WAKE_FREQ_10        = 0x3;

	public static final int MPU6050_BANKSEL_PRFTCH_EN_BIT     = 6;
	public static final int MPU6050_BANKSEL_CFG_USER_BANK_BIT = 5;
	public static final int MPU6050_BANKSEL_MEM_SEL_BIT       = 4;
	public static final int MPU6050_BANKSEL_MEM_SEL_LENGTH    = 5;

	public static final int MPU6050_WHO_AM_I_BIT      = 6;
	public static final int MPU6050_WHO_AM_I_LENGTH   = 6;

	public static final int MPU6050_DMP_MEMORY_BANKS      = 8;
	public static final int MPU6050_DMP_MEMORY_BANK_SIZE  = 256;
	public static final int MPU6050_DMP_MEMORY_CHUNK_SIZE = 16;
	
	public static final byte ACCEL_XOUT_H = 0x3B;
	
	public static int accelX;
	public static int accelY;
	public static int accelZ;
	public static double temperature;
	public static int gyroX;
	public static int gyroY;
	public static int gyroZ;
	
	// this block of memory gets written to the MPU on start-up, and it seems
	// to be volatile memory, so it has to be done each time (it only takes ~1
	// second though)
	public static final int[] dmpMemory = {
		    // bank 0, 256 bytes
		    0xFB, 0x00, 0x00, 0x3E, 0x00, 0x0B, 0x00, 0x36, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x00,
		    0x00, 0x65, 0x00, 0x54, 0xFF, 0xEF, 0x00, 0x00, 0xFA, 0x80, 0x00, 0x0B, 0x12, 0x82, 0x00, 0x01,
		    0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x28, 0x00, 0x00, 0xFF, 0xFF, 0x45, 0x81, 0xFF, 0xFF, 0xFA, 0x72, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x03, 0xE8, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x7F, 0xFF, 0xFF, 0xFE, 0x80, 0x01,
		    0x00, 0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x3E, 0x03, 0x30, 0x40, 0x00, 0x00, 0x00, 0x02, 0xCA, 0xE3, 0x09, 0x3E, 0x80, 0x00, 0x00,
		    0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x60, 0x00, 0x00, 0x00,
		    0x41, 0xFF, 0x00, 0x00, 0x00, 0x00, 0x0B, 0x2A, 0x00, 0x00, 0x16, 0x55, 0x00, 0x00, 0x21, 0x82,
		    0xFD, 0x87, 0x26, 0x50, 0xFD, 0x80, 0x00, 0x00, 0x00, 0x1F, 0x00, 0x00, 0x00, 0x05, 0x80, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00,
		    0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x6F, 0x00, 0x02, 0x65, 0x32, 0x00, 0x00, 0x5E, 0xC0,
		    0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0xFB, 0x8C, 0x6F, 0x5D, 0xFD, 0x5D, 0x08, 0xD9, 0x00, 0x7C, 0x73, 0x3B, 0x00, 0x6C, 0x12, 0xCC,
		    0x32, 0x00, 0x13, 0x9D, 0x32, 0x00, 0xD0, 0xD6, 0x32, 0x00, 0x08, 0x00, 0x40, 0x00, 0x01, 0xF4,
		    0xFF, 0xE6, 0x80, 0x79, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0xD0, 0xD6, 0x00, 0x00, 0x27, 0x10,

		    // bank 1, 256 bytes
		    0xFB, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0xFA, 0x36, 0xFF, 0xBC, 0x30, 0x8E, 0x00, 0x05, 0xFB, 0xF0, 0xFF, 0xD9, 0x5B, 0xC8,
		    0xFF, 0xD0, 0x9A, 0xBE, 0x00, 0x00, 0x10, 0xA9, 0xFF, 0xF4, 0x1E, 0xB2, 0x00, 0xCE, 0xBB, 0xF7,
		    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x04, 0x00, 0x02, 0x00, 0x02, 0x02, 0x00, 0x00, 0x0C,
		    0xFF, 0xC2, 0x80, 0x00, 0x00, 0x01, 0x80, 0x00, 0x00, 0xCF, 0x80, 0x00, 0x40, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x00, 0x14,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x03, 0x3F, 0x68, 0xB6, 0x79, 0x35, 0x28, 0xBC, 0xC6, 0x7E, 0xD1, 0x6C,
		    0x80, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0xB2, 0x6A, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x3F, 0xF0, 0x00, 0x00, 0x00, 0x30,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x25, 0x4D, 0x00, 0x2F, 0x70, 0x6D, 0x00, 0x00, 0x05, 0xAE, 0x00, 0x0C, 0x02, 0xD0,

		    // bank 2, 256 bytes
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x65, 0x00, 0x54, 0xFF, 0xEF, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x01, 0x00, 0x00, 0x44, 0x00, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x01, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x65, 0x00, 0x00, 0x00, 0x54, 0x00, 0x00, 0xFF, 0xEF, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00,
		    0x00, 0x1B, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,

		    // bank 3, 256 bytes
		    0xD8, 0xDC, 0xBA, 0xA2, 0xF1, 0xDE, 0xB2, 0xB8, 0xB4, 0xA8, 0x81, 0x91, 0xF7, 0x4A, 0x90, 0x7F,
		    0x91, 0x6A, 0xF3, 0xF9, 0xDB, 0xA8, 0xF9, 0xB0, 0xBA, 0xA0, 0x80, 0xF2, 0xCE, 0x81, 0xF3, 0xC2,
		    0xF1, 0xC1, 0xF2, 0xC3, 0xF3, 0xCC, 0xA2, 0xB2, 0x80, 0xF1, 0xC6, 0xD8, 0x80, 0xBA, 0xA7, 0xDF,
		    0xDF, 0xDF, 0xF2, 0xA7, 0xC3, 0xCB, 0xC5, 0xB6, 0xF0, 0x87, 0xA2, 0x94, 0x24, 0x48, 0x70, 0x3C,
		    0x95, 0x40, 0x68, 0x34, 0x58, 0x9B, 0x78, 0xA2, 0xF1, 0x83, 0x92, 0x2D, 0x55, 0x7D, 0xD8, 0xB1,
		    0xB4, 0xB8, 0xA1, 0xD0, 0x91, 0x80, 0xF2, 0x70, 0xF3, 0x70, 0xF2, 0x7C, 0x80, 0xA8, 0xF1, 0x01,
		    0xB0, 0x98, 0x87, 0xD9, 0x43, 0xD8, 0x86, 0xC9, 0x88, 0xBA, 0xA1, 0xF2, 0x0E, 0xB8, 0x97, 0x80,
		    0xF1, 0xA9, 0xDF, 0xDF, 0xDF, 0xAA, 0xDF, 0xDF, 0xDF, 0xF2, 0xAA, 0xC5, 0xCD, 0xC7, 0xA9, 0x0C,
		    0xC9, 0x2C, 0x97, 0x97, 0x97, 0x97, 0xF1, 0xA9, 0x89, 0x26, 0x46, 0x66, 0xB0, 0xB4, 0xBA, 0x80,
		    0xAC, 0xDE, 0xF2, 0xCA, 0xF1, 0xB2, 0x8C, 0x02, 0xA9, 0xB6, 0x98, 0x00, 0x89, 0x0E, 0x16, 0x1E,
		    0xB8, 0xA9, 0xB4, 0x99, 0x2C, 0x54, 0x7C, 0xB0, 0x8A, 0xA8, 0x96, 0x36, 0x56, 0x76, 0xF1, 0xB9,
		    0xAF, 0xB4, 0xB0, 0x83, 0xC0, 0xB8, 0xA8, 0x97, 0x11, 0xB1, 0x8F, 0x98, 0xB9, 0xAF, 0xF0, 0x24,
		    0x08, 0x44, 0x10, 0x64, 0x18, 0xF1, 0xA3, 0x29, 0x55, 0x7D, 0xAF, 0x83, 0xB5, 0x93, 0xAF, 0xF0,
		    0x00, 0x28, 0x50, 0xF1, 0xA3, 0x86, 0x9F, 0x61, 0xA6, 0xDA, 0xDE, 0xDF, 0xD9, 0xFA, 0xA3, 0x86,
		    0x96, 0xDB, 0x31, 0xA6, 0xD9, 0xF8, 0xDF, 0xBA, 0xA6, 0x8F, 0xC2, 0xC5, 0xC7, 0xB2, 0x8C, 0xC1,
		    0xB8, 0xA2, 0xDF, 0xDF, 0xDF, 0xA3, 0xDF, 0xDF, 0xDF, 0xD8, 0xD8, 0xF1, 0xB8, 0xA8, 0xB2, 0x86,

		    // bank 4, 256 bytes
		    0xB4, 0x98, 0x0D, 0x35, 0x5D, 0xB8, 0xAA, 0x98, 0xB0, 0x87, 0x2D, 0x35, 0x3D, 0xB2, 0xB6, 0xBA,
		    0xAF, 0x8C, 0x96, 0x19, 0x8F, 0x9F, 0xA7, 0x0E, 0x16, 0x1E, 0xB4, 0x9A, 0xB8, 0xAA, 0x87, 0x2C,
		    0x54, 0x7C, 0xB9, 0xA3, 0xDE, 0xDF, 0xDF, 0xA3, 0xB1, 0x80, 0xF2, 0xC4, 0xCD, 0xC9, 0xF1, 0xB8,
		    0xA9, 0xB4, 0x99, 0x83, 0x0D, 0x35, 0x5D, 0x89, 0xB9, 0xA3, 0x2D, 0x55, 0x7D, 0xB5, 0x93, 0xA3,
		    0x0E, 0x16, 0x1E, 0xA9, 0x2C, 0x54, 0x7C, 0xB8, 0xB4, 0xB0, 0xF1, 0x97, 0x83, 0xA8, 0x11, 0x84,
		    0xA5, 0x09, 0x98, 0xA3, 0x83, 0xF0, 0xDA, 0x24, 0x08, 0x44, 0x10, 0x64, 0x18, 0xD8, 0xF1, 0xA5,
		    0x29, 0x55, 0x7D, 0xA5, 0x85, 0x95, 0x02, 0x1A, 0x2E, 0x3A, 0x56, 0x5A, 0x40, 0x48, 0xF9, 0xF3,
		    0xA3, 0xD9, 0xF8, 0xF0, 0x98, 0x83, 0x24, 0x08, 0x44, 0x10, 0x64, 0x18, 0x97, 0x82, 0xA8, 0xF1,
		    0x11, 0xF0, 0x98, 0xA2, 0x24, 0x08, 0x44, 0x10, 0x64, 0x18, 0xDA, 0xF3, 0xDE, 0xD8, 0x83, 0xA5,
		    0x94, 0x01, 0xD9, 0xA3, 0x02, 0xF1, 0xA2, 0xC3, 0xC5, 0xC7, 0xD8, 0xF1, 0x84, 0x92, 0xA2, 0x4D,
		    0xDA, 0x2A, 0xD8, 0x48, 0x69, 0xD9, 0x2A, 0xD8, 0x68, 0x55, 0xDA, 0x32, 0xD8, 0x50, 0x71, 0xD9,
		    0x32, 0xD8, 0x70, 0x5D, 0xDA, 0x3A, 0xD8, 0x58, 0x79, 0xD9, 0x3A, 0xD8, 0x78, 0x93, 0xA3, 0x4D,
		    0xDA, 0x2A, 0xD8, 0x48, 0x69, 0xD9, 0x2A, 0xD8, 0x68, 0x55, 0xDA, 0x32, 0xD8, 0x50, 0x71, 0xD9,
		    0x32, 0xD8, 0x70, 0x5D, 0xDA, 0x3A, 0xD8, 0x58, 0x79, 0xD9, 0x3A, 0xD8, 0x78, 0xA8, 0x8A, 0x9A,
		    0xF0, 0x28, 0x50, 0x78, 0x9E, 0xF3, 0x88, 0x18, 0xF1, 0x9F, 0x1D, 0x98, 0xA8, 0xD9, 0x08, 0xD8,
		    0xC8, 0x9F, 0x12, 0x9E, 0xF3, 0x15, 0xA8, 0xDA, 0x12, 0x10, 0xD8, 0xF1, 0xAF, 0xC8, 0x97, 0x87,

		    // bank 5, 256 bytes
		    0x34, 0xB5, 0xB9, 0x94, 0xA4, 0x21, 0xF3, 0xD9, 0x22, 0xD8, 0xF2, 0x2D, 0xF3, 0xD9, 0x2A, 0xD8,
		    0xF2, 0x35, 0xF3, 0xD9, 0x32, 0xD8, 0x81, 0xA4, 0x60, 0x60, 0x61, 0xD9, 0x61, 0xD8, 0x6C, 0x68,
		    0x69, 0xD9, 0x69, 0xD8, 0x74, 0x70, 0x71, 0xD9, 0x71, 0xD8, 0xB1, 0xA3, 0x84, 0x19, 0x3D, 0x5D,
		    0xA3, 0x83, 0x1A, 0x3E, 0x5E, 0x93, 0x10, 0x30, 0x81, 0x10, 0x11, 0xB8, 0xB0, 0xAF, 0x8F, 0x94,
		    0xF2, 0xDA, 0x3E, 0xD8, 0xB4, 0x9A, 0xA8, 0x87, 0x29, 0xDA, 0xF8, 0xD8, 0x87, 0x9A, 0x35, 0xDA,
		    0xF8, 0xD8, 0x87, 0x9A, 0x3D, 0xDA, 0xF8, 0xD8, 0xB1, 0xB9, 0xA4, 0x98, 0x85, 0x02, 0x2E, 0x56,
		    0xA5, 0x81, 0x00, 0x0C, 0x14, 0xA3, 0x97, 0xB0, 0x8A, 0xF1, 0x2D, 0xD9, 0x28, 0xD8, 0x4D, 0xD9,
		    0x48, 0xD8, 0x6D, 0xD9, 0x68, 0xD8, 0xB1, 0x84, 0x0D, 0xDA, 0x0E, 0xD8, 0xA3, 0x29, 0x83, 0xDA,
		    0x2C, 0x0E, 0xD8, 0xA3, 0x84, 0x49, 0x83, 0xDA, 0x2C, 0x4C, 0x0E, 0xD8, 0xB8, 0xB0, 0xA8, 0x8A,
		    0x9A, 0xF5, 0x20, 0xAA, 0xDA, 0xDF, 0xD8, 0xA8, 0x40, 0xAA, 0xD0, 0xDA, 0xDE, 0xD8, 0xA8, 0x60,
		    0xAA, 0xDA, 0xD0, 0xDF, 0xD8, 0xF1, 0x97, 0x86, 0xA8, 0x31, 0x9B, 0x06, 0x99, 0x07, 0xAB, 0x97,
		    0x28, 0x88, 0x9B, 0xF0, 0x0C, 0x20, 0x14, 0x40, 0xB8, 0xB0, 0xB4, 0xA8, 0x8C, 0x9C, 0xF0, 0x04,
		    0x28, 0x51, 0x79, 0x1D, 0x30, 0x14, 0x38, 0xB2, 0x82, 0xAB, 0xD0, 0x98, 0x2C, 0x50, 0x50, 0x78,
		    0x78, 0x9B, 0xF1, 0x1A, 0xB0, 0xF0, 0x8A, 0x9C, 0xA8, 0x29, 0x51, 0x79, 0x8B, 0x29, 0x51, 0x79,
		    0x8A, 0x24, 0x70, 0x59, 0x8B, 0x20, 0x58, 0x71, 0x8A, 0x44, 0x69, 0x38, 0x8B, 0x39, 0x40, 0x68,
		    0x8A, 0x64, 0x48, 0x31, 0x8B, 0x30, 0x49, 0x60, 0xA5, 0x88, 0x20, 0x09, 0x71, 0x58, 0x44, 0x68,

		    // bank 6, 256 bytes
		    0x11, 0x39, 0x64, 0x49, 0x30, 0x19, 0xF1, 0xAC, 0x00, 0x2C, 0x54, 0x7C, 0xF0, 0x8C, 0xA8, 0x04,
		    0x28, 0x50, 0x78, 0xF1, 0x88, 0x97, 0x26, 0xA8, 0x59, 0x98, 0xAC, 0x8C, 0x02, 0x26, 0x46, 0x66,
		    0xF0, 0x89, 0x9C, 0xA8, 0x29, 0x51, 0x79, 0x24, 0x70, 0x59, 0x44, 0x69, 0x38, 0x64, 0x48, 0x31,
		    0xA9, 0x88, 0x09, 0x20, 0x59, 0x70, 0xAB, 0x11, 0x38, 0x40, 0x69, 0xA8, 0x19, 0x31, 0x48, 0x60,
		    0x8C, 0xA8, 0x3C, 0x41, 0x5C, 0x20, 0x7C, 0x00, 0xF1, 0x87, 0x98, 0x19, 0x86, 0xA8, 0x6E, 0x76,
		    0x7E, 0xA9, 0x99, 0x88, 0x2D, 0x55, 0x7D, 0x9E, 0xB9, 0xA3, 0x8A, 0x22, 0x8A, 0x6E, 0x8A, 0x56,
		    0x8A, 0x5E, 0x9F, 0xB1, 0x83, 0x06, 0x26, 0x46, 0x66, 0x0E, 0x2E, 0x4E, 0x6E, 0x9D, 0xB8, 0xAD,
		    0x00, 0x2C, 0x54, 0x7C, 0xF2, 0xB1, 0x8C, 0xB4, 0x99, 0xB9, 0xA3, 0x2D, 0x55, 0x7D, 0x81, 0x91,
		    0xAC, 0x38, 0xAD, 0x3A, 0xB5, 0x83, 0x91, 0xAC, 0x2D, 0xD9, 0x28, 0xD8, 0x4D, 0xD9, 0x48, 0xD8,
		    0x6D, 0xD9, 0x68, 0xD8, 0x8C, 0x9D, 0xAE, 0x29, 0xD9, 0x04, 0xAE, 0xD8, 0x51, 0xD9, 0x04, 0xAE,
		    0xD8, 0x79, 0xD9, 0x04, 0xD8, 0x81, 0xF3, 0x9D, 0xAD, 0x00, 0x8D, 0xAE, 0x19, 0x81, 0xAD, 0xD9,
		    0x01, 0xD8, 0xF2, 0xAE, 0xDA, 0x26, 0xD8, 0x8E, 0x91, 0x29, 0x83, 0xA7, 0xD9, 0xAD, 0xAD, 0xAD,
		    0xAD, 0xF3, 0x2A, 0xD8, 0xD8, 0xF1, 0xB0, 0xAC, 0x89, 0x91, 0x3E, 0x5E, 0x76, 0xF3, 0xAC, 0x2E,
		    0x2E, 0xF1, 0xB1, 0x8C, 0x5A, 0x9C, 0xAC, 0x2C, 0x28, 0x28, 0x28, 0x9C, 0xAC, 0x30, 0x18, 0xA8,
		    0x98, 0x81, 0x28, 0x34, 0x3C, 0x97, 0x24, 0xA7, 0x28, 0x34, 0x3C, 0x9C, 0x24, 0xF2, 0xB0, 0x89,
		    0xAC, 0x91, 0x2C, 0x4C, 0x6C, 0x8A, 0x9B, 0x2D, 0xD9, 0xD8, 0xD8, 0x51, 0xD9, 0xD8, 0xD8, 0x79,

		    // bank 7, 138 bytes (remainder)
		    0xD9, 0xD8, 0xD8, 0xF1, 0x9E, 0x88, 0xA3, 0x31, 0xDA, 0xD8, 0xD8, 0x91, 0x2D, 0xD9, 0x28, 0xD8,
		    0x4D, 0xD9, 0x48, 0xD8, 0x6D, 0xD9, 0x68, 0xD8, 0xB1, 0x83, 0x93, 0x35, 0x3D, 0x80, 0x25, 0xDA,
		    0xD8, 0xD8, 0x85, 0x69, 0xDA, 0xD8, 0xD8, 0xB4, 0x93, 0x81, 0xA3, 0x28, 0x34, 0x3C, 0xF3, 0xAB,
		    0x8B, 0xF8, 0xA3, 0x91, 0xB6, 0x09, 0xB4, 0xD9, 0xAB, 0xDE, 0xFA, 0xB0, 0x87, 0x9C, 0xB9, 0xA3,
		    0xDD, 0xF1, 0xA3, 0xA3, 0xA3, 0xA3, 0x95, 0xF1, 0xA3, 0xA3, 0xA3, 0x9D, 0xF1, 0xA3, 0xA3, 0xA3,
		    0xA3, 0xF2, 0xA3, 0xB4, 0x90, 0x80, 0xF2, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3,
		    0xA3, 0xB2, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3, 0xA3, 0xB0, 0x87, 0xB5, 0x99, 0xF1, 0xA3, 0xA3, 0xA3,
		    0x98, 0xF1, 0xA3, 0xA3, 0xA3, 0xA3, 0x97, 0xA3, 0xA3, 0xA3, 0xA3, 0xF3, 0x9B, 0xA3, 0xA3, 0xDC,
		    0xB9, 0xA7, 0xF1, 0x26, 0x26, 0x26, 0xD8, 0xD8, 0xFF
		};
	
	// thanks to Noah Zerkin for piecing this stuff together!
	public static final int[] dmpConfig = {
	//  BANK    OFFSET  LENGTH  [DATA]
	    0x03,   0x7B,   0x03,   0x4C, 0xCD, 0x6C,         // FCFG_1 inv_set_gyro_calibration
	    0x03,   0xAB,   0x03,   0x36, 0x56, 0x76,         // FCFG_3 inv_set_gyro_calibration
	    0x00,   0x68,   0x04,   0x02, 0xCB, 0x47, 0xA2,   // D_0_104 inv_set_gyro_calibration
	    0x02,   0x18,   0x04,   0x00, 0x05, 0x8B, 0xC1,   // D_0_24 inv_set_gyro_calibration
	    0x01,   0x0C,   0x04,   0x00, 0x00, 0x00, 0x00,   // D_1_152 inv_set_accel_calibration
	    0x03,   0x7F,   0x06,   0x0C, 0xC9, 0x2C, 0x97, 0x97, 0x97, // FCFG_2 inv_set_accel_calibration
	    0x03,   0x89,   0x03,   0x26, 0x46, 0x66,         // FCFG_7 inv_set_accel_calibration
	    0x00,   0x6C,   0x02,   0x20, 0x00,               // D_0_108 inv_set_accel_calibration
	    0x02,   0x40,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_00 inv_set_compass_calibration
	    0x02,   0x44,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_01
	    0x02,   0x48,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_02
	    0x02,   0x4C,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_10
	    0x02,   0x50,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_11
	    0x02,   0x54,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_12
	    0x02,   0x58,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_20
	    0x02,   0x5C,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_21
	    0x02,   0xBC,   0x04,   0x00, 0x00, 0x00, 0x00,   // CPASS_MTX_22
	    0x01,   0xEC,   0x04,   0x00, 0x00, 0x40, 0x00,   // D_1_236 inv_apply_endian_accel
	    0x03,   0x7F,   0x06,   0x0C, 0xC9, 0x2C, 0x97, 0x97, 0x97, // FCFG_2 inv_set_mpu_sensors
	    0x04,   0x02,   0x03,   0x0D, 0x35, 0x5D,         // CFG_MOTION_BIAS inv_turn_on_bias_from_no_motion
	    0x04,   0x09,   0x04,   0x87, 0x2D, 0x35, 0x3D,   // FCFG_5 inv_set_bias_update
	    0x00,   0xA3,   0x01,   0x00,                     // D_0_163 inv_set_dead_zone
	                 // SPECIAL 0x01 = enable interrupts
	    0x00,   0x00,   0x00,   0x01, // SET INT_ENABLE at i=22, SPECIAL INSTRUCTION
	    0x07,   0x86,   0x01,   0xFE,                     // CFG_6 inv_set_fifo_interupt
	    0x07,   0x41,   0x05,   0xF1, 0x20, 0x28, 0x30, 0x38, // CFG_8 inv_send_quaternion
	    0x07,   0x7E,   0x01,   0x30,                     // CFG_16 inv_set_footer
	    0x07,   0x46,   0x01,   0x9A,                     // CFG_GYRO_SOURCE inv_send_gyro
	    0x07,   0x47,   0x04,   0xF1, 0x28, 0x30, 0x38,   // CFG_9 inv_send_gyro -> inv_construct3_fifo
	    0x07,   0x6C,   0x04,   0xF1, 0x28, 0x30, 0x38,   // CFG_12 inv_send_accel -> inv_construct3_fifo
	    0x02,   0x16,   0x02,   0x00, 0x01                // D_0_22 inv_set_fifo_rate

	    // This very last 0x01 WAS a 0x09, which drops the FIFO rate down to 20 Hz. 0x07 is 25 Hz,
	    // 0x01 is 100Hz. Going faster than 100Hz (0x00=200Hz) tends to result in very noisy data.
	    // DMP output frequency is calculated easily using this equation: (200Hz / (1 + value))

	    // It is important to make sure the host processor can keep up with reading and processing
	    // the FIFO output at the desired rate. Handling FIFO overflow cleanly is also a good idea.
	};
	

	public static final int[] dmpUpdates = {
		0x01,   0xB2,   0x02,   0xFF, 0xFF,
		0x01,   0x90,   0x04,   0x09, 0x23, 0xA1, 0x35,
		0x01,   0x6A,   0x02,   0x06, 0x00,
		0x01,   0x60,   0x08,   0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00,   0x60,   0x04,   0x40, 0x00, 0x00, 0x00,
		0x01,   0x62,   0x02,   0x00, 0x00,
		0x00,   0x60,   0x04,   0x00, 0x40, 0x00, 0x00
	};

	private int[] buffer = new int[14];
	private int bytebuffer;
	int timeout = 0;
	
	public static void main(String[] args) {
		LoggingFactory.getInstance().configure();

		try {

			Mpu6050 mpu6050 = (Mpu6050) Runtime.start("mpu6050", "Mpu6050");
			Runtime.start("gui", "GUIService");
			/*
			Arduino arduino = (Arduino) Runtime.start("Arduino","Arduino");
			arduino.connect("COM4");
			mpu6050.setController(arduino);
			*/
			RasPi raspi = (RasPi) Runtime.start("RasPi","RasPi");
			mpu6050.setController(raspi);
			mpu6050.dmpInitialize();

		} catch (Exception e) {
			Logging.logError(e);
		}
	}

	public Mpu6050(String n) {
		super(n);
	}

	@Override
	public void startService() {
		super.startService();
	}
	
	public boolean setController(I2CControl controller) {
		if (controller == null) {
			error("setting null as controller");
			return false;
		}

		log.info(String.format("%s setController %s", getName(), controller.getName()));

		this.controller = controller;
		controller.createDevice(busAddress, deviceAddress, type);
		broadcastState();
		return true;
	}
	/**
	 * This method creates the i2c device 
	 */
	public boolean setDeviceAddress(int DeviceAddress){
		if (controller != null) {
			if (deviceAddress != DeviceAddress){
				controller.releaseDevice(busAddress,deviceAddress);
				controller.createDevice(busAddress, DeviceAddress, type);
			}
		}
		log.info(String.format("Setting device address to x%02X", deviceAddress));
		deviceAddress = DeviceAddress;
		return true;
	}
	/**
	 * This method reads all the 7 raw values in one go
	 * accelX
	 * accelY
	 * accelZ
	 * temperature ( In degrees Celcius )
	 * gyroX
	 * gyroY
	 * gyroZ
	 * 
	 */
	public void readRaw(){
		// Set the start address to read from
		byte[] writebuffer = {ACCEL_XOUT_H}; 
		controller.i2cWrite(busAddress, deviceAddress, writebuffer, writebuffer.length);
	    // The Arduino example executes a request_from() 
		// Not sure if this will do the trick
		// Request 14 bytes from the MPU-6050
		byte[] readbuffer = new byte[14]; 
		controller.i2cWrite(busAddress, deviceAddress, readbuffer, readbuffer.length);
		// Fill the variables with the result from the read operation
		accelX = readbuffer[0]<<8 + readbuffer[1] & 0xFF;
		accelY = readbuffer[2]<<8 + readbuffer[3] & 0xFF;
		accelZ = readbuffer[4]<<8 + readbuffer[5] & 0xFF;
		int temp = readbuffer[6]<<8 + readbuffer[7] & 0xFF;
		gyroX  = readbuffer[8]<<8 + readbuffer[9] & 0xFF;
		gyroY  = readbuffer[10]<<8 + readbuffer[11] & 0xFF;
		gyroZ  = readbuffer[12]<<8 + readbuffer[13] & 0xFF;
		// Convert temp to degrees Celcius
		temperature = temp / 340.0  + 36.53;		
	}
	
	public int dmpInitialize(){
	    // reset device
		log.info("Resetting MPU6050...");
	    reset();
	    delay(30); // wait after reset

	    // disable sleep mode
	    log.info("Disabling sleep mode...");
	    setSleepEnabled(false);
	    
	    // get MPU hardware revision
	    log.info("Selecting user bank 16...");
	    setMemoryBank(0x10, true, true);
	    log.info("Selecting memory byte 6...");
	    setMemoryStartAddress(0x06);
	    log.info("Checking hardware revision...");
	    log.info(String.format("Revision @ user[16][6] = x%02X", readMemoryByte()));
	    log.info("Resetting memory bank selection to 0...");
	    setMemoryBank(0, false, false);

	    // check OTP bank valid
	    log.info("Reading OTP bank valid flag...");
	    log.info(String.format("OTP bank is %b", getOTPBankValid()));

	    // get X/Y/Z gyro offsets
	    log.info("Reading gyro offset TC values...");
	    int xgOffsetTC = getXGyroOffsetTC();
	    int ygOffsetTC = getYGyroOffsetTC();
	    int zgOffsetTC = getZGyroOffsetTC();
	    log.info(String.format("X gyro offset = %s", xgOffsetTC));
	    log.info(String.format("Y gyro offset = %s", ygOffsetTC));
	    log.info(String.format("Z gyro offset = %S", zgOffsetTC));

	    // setup weird slave stuff (?)
	    log.info("Setting slave 0 address to 0x7F...");
	    setSlaveAddress(0, 0x7F);
	    log.info("Disabling I2C Master mode...");
	    setI2CMasterModeEnabled(false);
	    log.info("Setting slave 0 address to 0x68 (self)...");
	    setSlaveAddress(0, 0x68);
	    log.info("Resetting I2C Master control...");
	    resetI2CMaster();
	    delay(20);

	    // load DMP code into memory banks
	    log.info(String.format("Writing DMP code to MPU memory banks (%s) bytes", dmpMemory.length));
	    if (writeProgMemoryBlock(dmpMemory, dmpMemory.length)) {
	        log.info("Success! DMP code written and verified.");

	        // write DMP configuration
		    log.info(String.format("Writing DMP configuration to MPU memory banks (%s bytes in config def)",dmpConfig.length));
	        if (writeProgDMPConfigurationSet(dmpConfig, dmpConfig.length)) {
	            log.info("Success! DMP configuration written and verified.");

	            log.info("Setting clock source to Z Gyro...");
	            setClockSource(MPU6050_CLOCK_PLL_ZGYRO);

	            log.info("Setting DMP and FIFO_OFLOW interrupts enabled...");
	            setIntEnabled(0x12);

	            log.info("Setting sample rate to 200Hz...");
	            setRate(4); // 1khz / (1 + 4) = 200 Hz

	            log.info("Setting external frame sync to TEMP_OUT_L[0]...");
	            setExternalFrameSync(MPU6050_EXT_SYNC_TEMP_OUT_L);

	            log.info("Setting DLPF bandwidth to 42Hz...");
	            setDLPFMode(MPU6050_DLPF_BW_42);

	            log.info("Setting gyro sensitivity to +/- 2000 deg/sec...");
	            setFullScaleGyroRange(MPU6050_GYRO_FS_2000);

	            log.info("Setting DMP configuration bytes (function unknown)...");
	            setDMPConfig1(0x03);
	            setDMPConfig2(0x00);

	            log.info("Clearing OTP Bank flag...");
	            setOTPBankValid(false);

	            log.info("Setting X/Y/Z gyro offset TCs to previous values...");
	            setXGyroOffsetTC(xgOffsetTC);
	            setYGyroOffsetTC(ygOffsetTC);
	            setZGyroOffsetTC(zgOffsetTC);

	            //log.info("Setting X/Y/Z gyro user offsets to zero..."));
	            //setXGyroOffset(0);
	            //setYGyroOffset(0);
	            //setZGyroOffset(0);

	            log.info("Writing final memory update 1/7 (function unknown)...");
	            int[] dmpUpdate = new int[13];
	            int j;
	            int pos = 0;
	            int bank = 0, address = 0, dataSize = 0;
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);

	            log.info("Writing final memory update 2/7 (function unknown)...");
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);

	            log.info("Resetting FIFO...");
	            resetFIFO();

	            log.info("Reading FIFO count...");
	            int fifoCount = getFIFOCount();
	            int[] fifoBuffer = new int[128];

	            log.info(String.format("Current FIFO count=%s",fifoCount));
	            getFIFOBytes(fifoBuffer, fifoCount);

	            log.info("Setting motion detection threshold to 2...");
	            setMotionDetectionThreshold(2);

	            log.info("Setting zero-motion detection threshold to 156...");
	            setZeroMotionDetectionThreshold(156);

	            log.info("Setting motion detection duration to 80...");
	            setMotionDetectionDuration(80);

	            log.info("Setting zero-motion detection duration to 0...");
	            setZeroMotionDetectionDuration(0);

	            log.info("Resetting FIFO...");
	            resetFIFO();

	            log.info("Enabling FIFO...");
	            setFIFOEnabled(true);

	            log.info("Enabling DMP...");
	            setDMPEnabled(true);

	            log.info("Resetting DMP...");
	            resetDMP();

	            log.info("Writing final memory update 3/7 (function unknown)...");
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);

	            log.info("Writing final memory update 4/7 (function unknown)...");
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);
	            log.info("Writing final memory update 5/7 (function unknown)...");
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);
	            log.info("Waiting for FIFO count > 2...");
	            while ((fifoCount = getFIFOCount()) < 3);

	            log.info(String.format("Current FIFO count=%s",fifoCount));
	            log.info("Reading FIFO data...");
	            getFIFOBytes(fifoBuffer, fifoCount);

	            log.info("Reading interrupt status...");

	            log.info(String.format("Current interrupt status=x%02X",getIntStatus()));

	            log.info("Reading final memory update 6/7 (function unknown)...");
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);
	            log.info("Waiting for FIFO count > 2...");
	            while ((fifoCount = getFIFOCount()) < 3);

	            log.info(String.format("Current FIFO count=%s",fifoCount));

	            log.info("Reading FIFO data...");
	            getFIFOBytes(fifoBuffer, fifoCount);

	            log.info("Reading interrupt status...");

	            log.info(String.format("Current interrupt status=x%02X",getIntStatus()));

	            log.info("Writing final memory update 7/7 (function unknown)...");
	            for (j = 0; j < 4 || j < dmpUpdate[2] + 3; j++, pos++){
	            	if (j==0){
	            		bank = dmpUpdates[j];
	            		}
	            	if (j==1){ 
	            		address = dmpUpdates[j];
	            		}
	            	if (j==2){
	            		dataSize = dmpUpdates[j];
	            		}
	            	if (j>2){
	            		dmpUpdate[j-3] = dmpUpdates[pos];
	            	}
	            }
	            writeMemoryBlock(dmpUpdate, dataSize, bank, address,true);
	            log.info("DMP is good to go! Finally.");

	            log.info("Disabling DMP (you turn it on later)...");
	            setDMPEnabled(false);

	            log.info("Setting up internal 42-byte (default) DMP packet buffer...");
	            int dmpPacketSize = 42;
	            /*if ((dmpPacketBuffer = (int *)malloc(42)) == 0) {
	                return 3; // TODO: proper error code for no memory
	            }*/

	            log.info("Resetting FIFO and clearing INT status one last time...");
	            resetFIFO();
	            getIntStatus();
	        } else {
	            log.info("ERROR! DMP configuration verification failed.");
	            return 2; // configuration block loading failed
	        }
	    } else {
	        log.info("ERROR! DMP code verification failed.");
	        return 1; // main binary block loading failed
	    }
		return 0;
	}
	
	public void delay(int ms){
	    try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Logging.logError(e);
		} // wait after reset
	}
	public void initialize() {
	    setClockSource(MPU6050_CLOCK_PLL_XGYRO);
	    setFullScaleGyroRange(MPU6050_GYRO_FS_250);
	    setFullScaleAccelRange(MPU6050_ACCEL_FS_2);
	    setSleepEnabled(false); // thanks to Jack Elston for pointing this one out!
	}
	/** Verify the I2C connection.
	 * Make sure the device is connected and responds as expected.
	 * @return True if connection is valid, false otherwise
	 */
	public boolean testConnection() {
	    return getDeviceID() == 0x34;
	}

	// AUX_VDDIO register (InvenSense demo code calls this RA_*G_OFFS_TC)

	/** Get the auxiliary I2C supply voltage level.
	 * When set to 1, the auxiliary I2C bus high logic level is VDD. When cleared to
	 * 0, the auxiliary I2C bus high logic level is VLOGIC. This does not apply to
	 * the MPU-6000, which does not have a VLOGIC pin.
	 * @return I2C supply voltage level (0=VLOGIC, 1=VDD)
	 */
	public int getAuxVDDIOLevel() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_YG_OFFS_TC, MPU6050_TC_PWR_MODE_BIT, bytebuffer);
	    return bytebuffer;
	}
	/** Set the auxiliary I2C supply voltage level.
	 * When set to 1, the auxiliary I2C bus high logic level is VDD. When cleared to
	 * 0, the auxiliary I2C bus high logic level is VLOGIC. This does not apply to
	 * the MPU-6000, which does not have a VLOGIC pin.
	 * @param level I2C supply voltage level (0=VLOGIC, 1=VDD)
	 */
	void setAuxVDDIOLevel(int level) {
		boolean bitbuffer = (level != 0);
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_YG_OFFS_TC, MPU6050_TC_PWR_MODE_BIT, bitbuffer);
	}

	// SMPLRT_DIV register

	/** Get gyroscope output rate divider.
	 * The sensor register output, FIFO output, DMP sampling, Motion detection, Zero
	 * Motion detection, and Free Fall detection are all based on the Sample Rate.
	 * The Sample Rate is generated by dividing the gyroscope output rate by
	 * SMPLRT_DIV:
	 *
	 * Sample Rate = Gyroscope Output Rate / (1 + SMPLRT_DIV)
	 *
	 * where Gyroscope Output Rate = 8kHz when the DLPF is disabled (DLPF_CFG = 0 or
	 * 7), and 1kHz when the DLPF is enabled (see Register 26).
	 *
	 * Note: The accelerometer output rate is 1kHz. This means that for a Sample
	 * Rate greater than 1kHz, the same accelerometer sample may be output to the
	 * FIFO, DMP, and sensor registers more than once.
	 *
	 * For a diagram of the gyroscope and accelerometer signal paths, see Section 8
	 * of the MPU-6000/MPU-6050 Product Specification document.
	 *
	 * @return Current sample rate
	 * @see MPU6050_RA_SMPLRT_DIV
	 */
	int getRate() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_SMPLRT_DIV, bytebuffer);
	    return bytebuffer;
	}
	/** Set gyroscope sample rate divider.
	 * @param rate New sample rate divider
	 * @see getRate()
	 * @see MPU6050_RA_SMPLRT_DIV
	 */
	void setRate(int rate) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_SMPLRT_DIV, rate);
	}

	// CONFIG register

	/** Get external FSYNC configuration.
	 * Configures the external Frame Synchronization (FSYNC) pin sampling. An
	 * external signal connected to the FSYNC pin can be sampled by configuring
	 * EXT_SYNC_SET. Signal changes to the FSYNC pin are latched so that short
	 * strobes may be captured. The latched FSYNC signal will be sampled at the
	 * Sampling Rate, as defined in register 25. After sampling, the latch will
	 * reset to the current FSYNC signal state.
	 *
	 * The sampled value will be reported in place of the least significant bit in
	 * a sensor data register determined by the value of EXT_SYNC_SET according to
	 * the following table.
	 *
	 * <pre>
	 * EXT_SYNC_SET | FSYNC Bit Location
	 * -------------+-------------------
	 * 0            | Input disabled
	 * 1            | TEMP_OUT_L[0]
	 * 2            | GYRO_XOUT_L[0]
	 * 3            | GYRO_YOUT_L[0]
	 * 4            | GYRO_ZOUT_L[0]
	 * 5            | ACCEL_XOUT_L[0]
	 * 6            | ACCEL_YOUT_L[0]
	 * 7            | ACCEL_ZOUT_L[0]
	 * </pre>
	 *
	 * @return FSYNC configuration value
	 */
	int getExternalFrameSync() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_CONFIG, MPU6050_CFG_EXT_SYNC_SET_BIT, MPU6050_CFG_EXT_SYNC_SET_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set external FSYNC configuration.
	 * @see getExternalFrameSync()
	 * @see MPU6050_RA_CONFIG
	 * @param sync New FSYNC configuration value
	 */
	void setExternalFrameSync(int sync) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_CONFIG, MPU6050_CFG_EXT_SYNC_SET_BIT, MPU6050_CFG_EXT_SYNC_SET_LENGTH, sync);
	}
	/** Get digital low-pass filter configuration.
	 * The DLPF_CFG parameter sets the digital low pass filter configuration. It
	 * also determines the internal sampling rate used by the device as shown in
	 * the table below.
	 *
	 * Note: The accelerometer output rate is 1kHz. This means that for a Sample
	 * Rate greater than 1kHz, the same accelerometer sample may be output to the
	 * FIFO, DMP, and sensor registers more than once.
	 *
	 * <pre>
	 *          |   ACCELEROMETER    |           GYROSCOPE
	 * DLPF_CFG | Bandwidth | Delay  | Bandwidth | Delay  | Sample Rate
	 * ---------+-----------+--------+-----------+--------+-------------
	 * 0        | 260Hz     | 0ms    | 256Hz     | 0.98ms | 8kHz
	 * 1        | 184Hz     | 2.0ms  | 188Hz     | 1.9ms  | 1kHz
	 * 2        | 94Hz      | 3.0ms  | 98Hz      | 2.8ms  | 1kHz
	 * 3        | 44Hz      | 4.9ms  | 42Hz      | 4.8ms  | 1kHz
	 * 4        | 21Hz      | 8.5ms  | 20Hz      | 8.3ms  | 1kHz
	 * 5        | 10Hz      | 13.8ms | 10Hz      | 13.4ms | 1kHz
	 * 6        | 5Hz       | 19.0ms | 5Hz       | 18.6ms | 1kHz
	 * 7        |   -- Reserved --   |   -- Reserved --   | Reserved
	 * </pre>
	 *
	 * @return DLFP configuration
	 * @see MPU6050_RA_CONFIG
	 * @see MPU6050_CFG_DLPF_CFG_BIT
	 * @see MPU6050_CFG_DLPF_CFG_LENGTH
	 */
	int getDLPFMode() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_CONFIG, MPU6050_CFG_DLPF_CFG_BIT, MPU6050_CFG_DLPF_CFG_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set digital low-pass filter configuration.
	 * @param mode New DLFP configuration setting
	 * @see getDLPFBandwidth()
	 * @see MPU6050_DLPF_BW_256
	 * @see MPU6050_RA_CONFIG
	 * @see MPU6050_CFG_DLPF_CFG_BIT
	 * @see MPU6050_CFG_DLPF_CFG_LENGTH
	 */
	void setDLPFMode(int mode) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_CONFIG, MPU6050_CFG_DLPF_CFG_BIT, MPU6050_CFG_DLPF_CFG_LENGTH, mode);
	}

	// GYRO_CONFIG register

	/** Get full-scale gyroscope range.
	 * The FS_SEL parameter allows setting the full-scale range of the gyro sensors,
	 * as described in the table below.
	 *
	 * <pre>
	 * 0 = +/- 250 degrees/sec
	 * 1 = +/- 500 degrees/sec
	 * 2 = +/- 1000 degrees/sec
	 * 3 = +/- 2000 degrees/sec
	 * </pre>
	 *
	 * @return Current full-scale gyroscope range setting
	 * @see MPU6050_GYRO_FS_250
	 * @see MPU6050_RA_GYRO_CONFIG
	 * @see MPU6050_GCONFIG_FS_SEL_BIT
	 * @see MPU6050_GCONFIG_FS_SEL_LENGTH
	 */
	int getFullScaleGyroRange() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_GYRO_CONFIG, MPU6050_GCONFIG_FS_SEL_BIT, MPU6050_GCONFIG_FS_SEL_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set full-scale gyroscope range.
	 * @param range New full-scale gyroscope range value
	 * @see getFullScaleRange()
	 * @see MPU6050_GYRO_FS_250
	 * @see MPU6050_RA_GYRO_CONFIG
	 * @see MPU6050_GCONFIG_FS_SEL_BIT
	 * @see MPU6050_GCONFIG_FS_SEL_LENGTH
	 */
	void setFullScaleGyroRange(int range) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_GYRO_CONFIG, MPU6050_GCONFIG_FS_SEL_BIT, MPU6050_GCONFIG_FS_SEL_LENGTH, range);
	}

	// SELF TEST FACTORY TRIM VALUES

	/** Get self-test factory trim value for accelerometer X axis.
	 * @return factory trim value
	 * @see MPU6050_RA_SELF_TEST_X
	 */
	int getAccelXSelfTestFactoryTrim() {
		int buffer0 = 0;
		int buffer1 = 0;
	    I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_X, buffer0);
		I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_A, buffer1);	
	    return (buffer0>>3) | ((buffer1>>4) & 0x03);
	}

	/** Get self-test factory trim value for accelerometer Y axis.
	 * @return factory trim value
	 * @see MPU6050_RA_SELF_TEST_Y
	 */
	int getAccelYSelfTestFactoryTrim() {
		int buffer0 = 0;
		int buffer1 = 0;
	    I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_Y, buffer0);
		I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_A, buffer1);	
	    return (buffer0>>3) | ((buffer1>>2) & 0x03);
	}

	/** Get self-test factory trim value for accelerometer Z axis.
	 * @return factory trim value
	 * @see MPU6050_RA_SELF_TEST_Z
	 */
	int getAccelZSelfTestFactoryTrim() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_SELF_TEST_Z, 2, buffer);	
	    return (buffer[0]>>3) | (buffer[1] & 0x03);
	}

	/** Get self-test factory trim value for gyro X axis.
	 * @return factory trim value
	 * @see MPU6050_RA_SELF_TEST_X
	 */
	int getGyroXSelfTestFactoryTrim() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_X, bytebuffer);	
	    return (bytebuffer & 0x1F);
	}

	/** Get self-test factory trim value for gyro Y axis.
	 * @return factory trim value
	 * @see MPU6050_RA_SELF_TEST_Y
	 */
	int getGyroYSelfTestFactoryTrim() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_Y, bytebuffer);	
	    return (bytebuffer & 0x1F);
	}

	/** Get self-test factory trim value for gyro Z axis.
	 * @return factory trim value
	 * @see MPU6050_RA_SELF_TEST_Z
	 */
	int getGyroZSelfTestFactoryTrim() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_SELF_TEST_Z, bytebuffer);	
	    return (bytebuffer & 0x1F);
	}

	// ACCEL_CONFIG register

	/** Get self-test enabled setting for accelerometer X axis.
	 * @return Self-test enabled value
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	boolean getAccelXSelfTest() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_XA_ST_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get self-test enabled setting for accelerometer X axis.
	 * @param enabled Self-test enabled value
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	void setAccelXSelfTest(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_XA_ST_BIT, enabled);
	}
	/** Get self-test enabled value for accelerometer Y axis.
	 * @return Self-test enabled value
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	boolean getAccelYSelfTest() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_YA_ST_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get self-test enabled value for accelerometer Y axis.
	 * @param enabled Self-test enabled value
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	void setAccelYSelfTest(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_YA_ST_BIT, enabled);
	}
	/** Get self-test enabled value for accelerometer Z axis.
	 * @return Self-test enabled value
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	boolean getAccelZSelfTest() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_ZA_ST_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set self-test enabled value for accelerometer Z axis.
	 * @param enabled Self-test enabled value
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	void setAccelZSelfTest(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_ZA_ST_BIT, enabled);
	}
	/** Get full-scale accelerometer range.
	 * The FS_SEL parameter allows setting the full-scale range of the accelerometer
	 * sensors, as described in the table below.
	 *
	 * <pre>
	 * 0 = +/- 2g
	 * 1 = +/- 4g
	 * 2 = +/- 8g
	 * 3 = +/- 16g
	 * </pre>
	 *
	 * @return Current full-scale accelerometer range setting
	 * @see MPU6050_ACCEL_FS_2
	 * @see MPU6050_RA_ACCEL_CONFIG
	 * @see MPU6050_ACONFIG_AFS_SEL_BIT
	 * @see MPU6050_ACONFIG_AFS_SEL_LENGTH
	 */
	int getFullScaleAccelRange() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_AFS_SEL_BIT, MPU6050_ACONFIG_AFS_SEL_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set full-scale accelerometer range.
	 * @param range New full-scale accelerometer range setting
	 * @see getFullScaleAccelRange()
	 */
	void setFullScaleAccelRange(int range) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_AFS_SEL_BIT, MPU6050_ACONFIG_AFS_SEL_LENGTH, range);
	}
	/** Get the high-pass filter configuration.
	 * The DHPF is a filter module in the path leading to motion detectors (Free
	 * Fall, Motion threshold, and Zero Motion). The high pass filter output is not
	 * available to the data registers (see Figure in Section 8 of the MPU-6000/
	 * MPU-6050 Product Specification document).
	 *
	 * The high pass filter has three modes:
	 *
	 * <pre>
	 *    Reset: The filter output settles to zero within one sample. This
	 *           effectively disables the high pass filter. This mode may be toggled
	 *           to quickly settle the filter.
	 *
	 *    On:    The high pass filter will pass signals above the cut off frequency.
	 *
	 *    Hold:  When triggered, the filter holds the present sample. The filter
	 *           output will be the difference between the input sample and the held
	 *           sample.
	 * </pre>
	 *
	 * <pre>
	 * ACCEL_HPF | Filter Mode | Cut-off Frequency
	 * ----------+-------------+------------------
	 * 0         | Reset       | None
	 * 1         | On          | 5Hz
	 * 2         | On          | 2.5Hz
	 * 3         | On          | 1.25Hz
	 * 4         | On          | 0.63Hz
	 * 7         | Hold        | None
	 * </pre>
	 *
	 * @return Current high-pass filter configuration
	 * @see MPU6050_DHPF_RESET
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	int getDHPFMode() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_ACCEL_HPF_BIT, MPU6050_ACONFIG_ACCEL_HPF_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set the high-pass filter configuration.
	 * @param bandwidth New high-pass filter configuration
	 * @see setDHPFMode()
	 * @see MPU6050_DHPF_RESET
	 * @see MPU6050_RA_ACCEL_CONFIG
	 */
	void setDHPFMode(int bandwidth) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_ACCEL_CONFIG, MPU6050_ACONFIG_ACCEL_HPF_BIT, MPU6050_ACONFIG_ACCEL_HPF_LENGTH, bandwidth);
	}

	// FF_THR register

	/** Get free-fall event acceleration threshold.
	 * This register configures the detection threshold for Free Fall event
	 * detection. The unit of FF_THR is 1LSB = 2mg. Free Fall is detected when the
	 * absolute value of the accelerometer measurements for the three axes are each
	 * less than the detection threshold. This condition increments the Free Fall
	 * duration counter (Register 30). The Free Fall interrupt is triggered when the
	 * Free Fall duration counter reaches the time specified in FF_DUR.
	 *
	 * For more details on the Free Fall detection interrupt, see Section 8.2 of the
	 * MPU-6000/MPU-6050 Product Specification document as well as Registers 56 and
	 * 58 of this document.
	 *
	 * @return Current free-fall acceleration threshold value (LSB = 2mg)
	 * @see MPU6050_RA_FF_THR
	 */
	int getFreefallDetectionThreshold() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_FF_THR, bytebuffer);
	    return bytebuffer;
	}
	/** Get free-fall event acceleration threshold.
	 * @param threshold New free-fall acceleration threshold value (LSB = 2mg)
	 * @see getFreefallDetectionThreshold()
	 * @see MPU6050_RA_FF_THR
	 */
	void setFreefallDetectionThreshold(int threshold) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_FF_THR, threshold);
	}

	// FF_DUR register

	/** Get free-fall event duration threshold.
	 * This register configures the duration counter threshold for Free Fall event
	 * detection. The duration counter ticks at 1kHz, therefore FF_DUR has a unit
	 * of 1 LSB = 1 ms.
	 *
	 * The Free Fall duration counter increments while the absolute value of the
	 * accelerometer measurements are each less than the detection threshold
	 * (Register 29). The Free Fall interrupt is triggered when the Free Fall
	 * duration counter reaches the time specified in this register.
	 *
	 * For more details on the Free Fall detection interrupt, see Section 8.2 of
	 * the MPU-6000/MPU-6050 Product Specification document as well as Registers 56
	 * and 58 of this document.
	 *
	 * @return Current free-fall duration threshold value (LSB = 1ms)
	 * @see MPU6050_RA_FF_DUR
	 */
	int getFreefallDetectionDuration() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_FF_DUR, bytebuffer);
	    return bytebuffer;
	}
	/** Get free-fall event duration threshold.
	 * @param duration New free-fall duration threshold value (LSB = 1ms)
	 * @see getFreefallDetectionDuration()
	 * @see MPU6050_RA_FF_DUR
	 */
	void setFreefallDetectionDuration(int duration) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_FF_DUR, duration);
	}

	// MOT_THR register

	/** Get motion detection event acceleration threshold.
	 * This register configures the detection threshold for Motion interrupt
	 * generation. The unit of MOT_THR is 1LSB = 2mg. Motion is detected when the
	 * absolute value of any of the accelerometer measurements exceeds this Motion
	 * detection threshold. This condition increments the Motion detection duration
	 * counter (Register 32). The Motion detection interrupt is triggered when the
	 * Motion Detection counter reaches the time count specified in MOT_DUR
	 * (Register 32).
	 *
	 * The Motion interrupt will indicate the axis and polarity of detected motion
	 * in MOT_DETECT_STATUS (Register 97).
	 *
	 * For more details on the Motion detection interrupt, see Section 8.3 of the
	 * MPU-6000/MPU-6050 Product Specification document as well as Registers 56 and
	 * 58 of this document.
	 *
	 * @return Current motion detection acceleration threshold value (LSB = 2mg)
	 * @see MPU6050_RA_MOT_THR
	 */
	int getMotionDetectionThreshold() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_MOT_THR, bytebuffer);
	    return bytebuffer;
	}
	/** Set motion detection event acceleration threshold.
	 * @param threshold New motion detection acceleration threshold value (LSB = 2mg)
	 * @see getMotionDetectionThreshold()
	 * @see MPU6050_RA_MOT_THR
	 */
	void setMotionDetectionThreshold(int threshold) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_MOT_THR, threshold);
	}

	// MOT_DUR register

	/** Get motion detection event duration threshold.
	 * This register configures the duration counter threshold for Motion interrupt
	 * generation. The duration counter ticks at 1 kHz, therefore MOT_DUR has a unit
	 * of 1LSB = 1ms. The Motion detection duration counter increments when the
	 * absolute value of any of the accelerometer measurements exceeds the Motion
	 * detection threshold (Register 31). The Motion detection interrupt is
	 * triggered when the Motion detection counter reaches the time count specified
	 * in this register.
	 *
	 * For more details on the Motion detection interrupt, see Section 8.3 of the
	 * MPU-6000/MPU-6050 Product Specification document.
	 *
	 * @return Current motion detection duration threshold value (LSB = 1ms)
	 * @see MPU6050_RA_MOT_DUR
	 */
	int getMotionDetectionDuration() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_MOT_DUR, bytebuffer);
	    return bytebuffer;
	}
	/** Set motion detection event duration threshold.
	 * @param duration New motion detection duration threshold value (LSB = 1ms)
	 * @see getMotionDetectionDuration()
	 * @see MPU6050_RA_MOT_DUR
	 */
	void setMotionDetectionDuration(int duration) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_MOT_DUR, duration);
	}

	// ZRMOT_THR register

	/** Get zero motion detection event acceleration threshold.
	 * This register configures the detection threshold for Zero Motion interrupt
	 * generation. The unit of ZRMOT_THR is 1LSB = 2mg. Zero Motion is detected when
	 * the absolute value of the accelerometer measurements for the 3 axes are each
	 * less than the detection threshold. This condition increments the Zero Motion
	 * duration counter (Register 34). The Zero Motion interrupt is triggered when
	 * the Zero Motion duration counter reaches the time count specified in
	 * ZRMOT_DUR (Register 34).
	 *
	 * Unlike Free Fall or Motion detection, Zero Motion detection triggers an
	 * interrupt both when Zero Motion is first detected and when Zero Motion is no
	 * longer detected.
	 *
	 * When a zero motion event is detected, a Zero Motion Status will be indicated
	 * in the MOT_DETECT_STATUS register (Register 97). When a motion-to-zero-motion
	 * condition is detected, the status bit is set to 1. When a zero-motion-to-
	 * motion condition is detected, the status bit is set to 0.
	 *
	 * For more details on the Zero Motion detection interrupt, see Section 8.4 of
	 * the MPU-6000/MPU-6050 Product Specification document as well as Registers 56
	 * and 58 of this document.
	 *
	 * @return Current zero motion detection acceleration threshold value (LSB = 2mg)
	 * @see MPU6050_RA_ZRMOT_THR
	 */
	int getZeroMotionDetectionThreshold() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_ZRMOT_THR, bytebuffer);
	    return bytebuffer;
	}
	/** Set zero motion detection event acceleration threshold.
	 * @param threshold New zero motion detection acceleration threshold value (LSB = 2mg)
	 * @see getZeroMotionDetectionThreshold()
	 * @see MPU6050_RA_ZRMOT_THR
	 */
	void setZeroMotionDetectionThreshold(int threshold) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_ZRMOT_THR, threshold);
	}

	// ZRMOT_DUR register

	/** Get zero motion detection event duration threshold.
	 * This register configures the duration counter threshold for Zero Motion
	 * interrupt generation. The duration counter ticks at 16 Hz, therefore
	 * ZRMOT_DUR has a unit of 1 LSB = 64 ms. The Zero Motion duration counter
	 * increments while the absolute value of the accelerometer measurements are
	 * each less than the detection threshold (Register 33). The Zero Motion
	 * interrupt is triggered when the Zero Motion duration counter reaches the time
	 * count specified in this register.
	 *
	 * For more details on the Zero Motion detection interrupt, see Section 8.4 of
	 * the MPU-6000/MPU-6050 Product Specification document, as well as Registers 56
	 * and 58 of this document.
	 *
	 * @return Current zero motion detection duration threshold value (LSB = 64ms)
	 * @see MPU6050_RA_ZRMOT_DUR
	 */
	int getZeroMotionDetectionDuration() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_ZRMOT_DUR, bytebuffer);
	    return bytebuffer;
	}
	/** Set zero motion detection event duration threshold.
	 * @param duration New zero motion detection duration threshold value (LSB = 1ms)
	 * @see getZeroMotionDetectionDuration()
	 * @see MPU6050_RA_ZRMOT_DUR
	 */
	void setZeroMotionDetectionDuration(int duration) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_ZRMOT_DUR, duration);
	}

	// FIFO_EN register

	/** Get temperature FIFO enabled value.
	 * When set to 1, this bit enables TEMP_OUT_H and TEMP_OUT_L (Registers 65 and
	 * 66) to be written into the FIFO buffer.
	 * @return Current temperature FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getTempFIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_TEMP_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set temperature FIFO enabled value.
	 * @param enabled New temperature FIFO enabled value
	 * @see getTempFIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setTempFIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_TEMP_FIFO_EN_BIT, enabled);
	}
	/** Get gyroscope X-axis FIFO enabled value.
	 * When set to 1, this bit enables GYRO_XOUT_H and GYRO_XOUT_L (Registers 67 and
	 * 68) to be written into the FIFO buffer.
	 * @return Current gyroscope X-axis FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getXGyroFIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_XG_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set gyroscope X-axis FIFO enabled value.
	 * @param enabled New gyroscope X-axis FIFO enabled value
	 * @see getXGyroFIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setXGyroFIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_XG_FIFO_EN_BIT, enabled);
	}
	/** Get gyroscope Y-axis FIFO enabled value.
	 * When set to 1, this bit enables GYRO_YOUT_H and GYRO_YOUT_L (Registers 69 and
	 * 70) to be written into the FIFO buffer.
	 * @return Current gyroscope Y-axis FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getYGyroFIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_YG_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set gyroscope Y-axis FIFO enabled value.
	 * @param enabled New gyroscope Y-axis FIFO enabled value
	 * @see getYGyroFIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setYGyroFIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_YG_FIFO_EN_BIT, enabled);
	}
	/** Get gyroscope Z-axis FIFO enabled value.
	 * When set to 1, this bit enables GYRO_ZOUT_H and GYRO_ZOUT_L (Registers 71 and
	 * 72) to be written into the FIFO buffer.
	 * @return Current gyroscope Z-axis FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getZGyroFIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_ZG_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set gyroscope Z-axis FIFO enabled value.
	 * @param enabled New gyroscope Z-axis FIFO enabled value
	 * @see getZGyroFIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setZGyroFIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_ZG_FIFO_EN_BIT, enabled);
	}
	/** Get accelerometer FIFO enabled value.
	 * When set to 1, this bit enables ACCEL_XOUT_H, ACCEL_XOUT_L, ACCEL_YOUT_H,
	 * ACCEL_YOUT_L, ACCEL_ZOUT_H, and ACCEL_ZOUT_L (Registers 59 to 64) to be
	 * written into the FIFO buffer.
	 * @return Current accelerometer FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getAccelFIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_ACCEL_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set accelerometer FIFO enabled value.
	 * @param enabled New accelerometer FIFO enabled value
	 * @see getAccelFIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setAccelFIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_ACCEL_FIFO_EN_BIT, enabled);
	}
	/** Get Slave 2 FIFO enabled value.
	 * When set to 1, this bit enables EXT_SENS_DATA registers (Registers 73 to 96)
	 * associated with Slave 2 to be written into the FIFO buffer.
	 * @return Current Slave 2 FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getSlave2FIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_SLV2_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Slave 2 FIFO enabled value.
	 * @param enabled New Slave 2 FIFO enabled value
	 * @see getSlave2FIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setSlave2FIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_SLV2_FIFO_EN_BIT, enabled);
	}
	/** Get Slave 1 FIFO enabled value.
	 * When set to 1, this bit enables EXT_SENS_DATA registers (Registers 73 to 96)
	 * associated with Slave 1 to be written into the FIFO buffer.
	 * @return Current Slave 1 FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getSlave1FIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_SLV1_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Slave 1 FIFO enabled value.
	 * @param enabled New Slave 1 FIFO enabled value
	 * @see getSlave1FIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setSlave1FIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_SLV1_FIFO_EN_BIT, enabled);
	}
	/** Get Slave 0 FIFO enabled value.
	 * When set to 1, this bit enables EXT_SENS_DATA registers (Registers 73 to 96)
	 * associated with Slave 0 to be written into the FIFO buffer.
	 * @return Current Slave 0 FIFO enabled value
	 * @see MPU6050_RA_FIFO_EN
	 */
	boolean getSlave0FIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_SLV0_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Slave 0 FIFO enabled value.
	 * @param enabled New Slave 0 FIFO enabled value
	 * @see getSlave0FIFOEnabled()
	 * @see MPU6050_RA_FIFO_EN
	 */
	void setSlave0FIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_FIFO_EN, MPU6050_SLV0_FIFO_EN_BIT, enabled);
	}

	// I2C_MST_CTRL register

	/** Get multi-master enabled value.
	 * Multi-master capability allows multiple I2C masters to operate on the same
	 * bus. In circuits where multi-master capability is required, set MULT_MST_EN
	 * to 1. This will increase current drawn by approximately 30uA.
	 *
	 * In circuits where multi-master capability is required, the state of the I2C
	 * bus must always be monitored by each separate I2C Master. Before an I2C
	 * Master can assume arbitration of the bus, it must first confirm that no other
	 * I2C Master has arbitration of the bus. When MULT_MST_EN is set to 1, the
	 * MPU-60X0's bus arbitration detection logic is turned on, enabling it to
	 * detect when the bus is available.
	 *
	 * @return Current multi-master enabled value
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	boolean getMultiMasterEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_MULT_MST_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set multi-master enabled value.
	 * @param enabled New multi-master enabled value
	 * @see getMultiMasterEnabled()
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	void setMultiMasterEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_MULT_MST_EN_BIT, enabled);
	}
	/** Get wait-for-external-sensor-data enabled value.
	 * When the WAIT_FOR_ES bit is set to 1, the Data Ready interrupt will be
	 * delayed until External Sensor data from the Slave Devices are loaded into the
	 * EXT_SENS_DATA registers. This is used to ensure that both the internal sensor
	 * data (i.e. from gyro and accel) and external sensor data have been loaded to
	 * their respective data registers (i.e. the data is synced) when the Data Ready
	 * interrupt is triggered.
	 *
	 * @return Current wait-for-external-sensor-data enabled value
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	boolean getWaitForExternalSensorEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_WAIT_FOR_ES_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set wait-for-external-sensor-data enabled value.
	 * @param enabled New wait-for-external-sensor-data enabled value
	 * @see getWaitForExternalSensorEnabled()
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	void setWaitForExternalSensorEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_WAIT_FOR_ES_BIT, enabled);
	}
	/** Get Slave 3 FIFO enabled value.
	 * When set to 1, this bit enables EXT_SENS_DATA registers (Registers 73 to 96)
	 * associated with Slave 3 to be written into the FIFO buffer.
	 * @return Current Slave 3 FIFO enabled value
	 * @see MPU6050_RA_MST_CTRL
	 */
	boolean getSlave3FIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_SLV_3_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Slave 3 FIFO enabled value.
	 * @param enabled New Slave 3 FIFO enabled value
	 * @see getSlave3FIFOEnabled()
	 * @see MPU6050_RA_MST_CTRL
	 */
	void setSlave3FIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_SLV_3_FIFO_EN_BIT, enabled);
	}
	/** Get slave read/write transition enabled value.
	 * The I2C_MST_P_NSR bit configures the I2C Master's transition from one slave
	 * read to the next slave read. If the bit equals 0, there will be a restart
	 * between reads. If the bit equals 1, there will be a stop followed by a start
	 * of the following read. When a write transaction follows a read transaction,
	 * the stop followed by a start of the successive write will be always used.
	 *
	 * @return Current slave read/write transition enabled value
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	boolean getSlaveReadWriteTransitionEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_I2C_MST_P_NSR_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set slave read/write transition enabled value.
	 * @param enabled New slave read/write transition enabled value
	 * @see getSlaveReadWriteTransitionEnabled()
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	void setSlaveReadWriteTransitionEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_I2C_MST_P_NSR_BIT, enabled);
	}
	/** Get I2C master clock speed.
	 * I2C_MST_CLK is a 4 bit unsigned value which configures a divider on the
	 * MPU-60X0 internal 8MHz clock. It sets the I2C master clock speed according to
	 * the following table:
	 *
	 * <pre>
	 * I2C_MST_CLK | I2C Master Clock Speed | 8MHz Clock Divider
	 * ------------+------------------------+-------------------
	 * 0           | 348kHz                 | 23
	 * 1           | 333kHz                 | 24
	 * 2           | 320kHz                 | 25
	 * 3           | 308kHz                 | 26
	 * 4           | 296kHz                 | 27
	 * 5           | 286kHz                 | 28
	 * 6           | 276kHz                 | 29
	 * 7           | 267kHz                 | 30
	 * 8           | 258kHz                 | 31
	 * 9           | 500kHz                 | 16
	 * 10          | 471kHz                 | 17
	 * 11          | 444kHz                 | 18
	 * 12          | 421kHz                 | 19
	 * 13          | 400kHz                 | 20
	 * 14          | 381kHz                 | 21
	 * 15          | 364kHz                 | 22
	 * </pre>
	 *
	 * @return Current I2C master clock speed
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	int getMasterClockSpeed() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_I2C_MST_CLK_BIT, MPU6050_I2C_MST_CLK_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set I2C master clock speed.
	 * @reparam speed Current I2C master clock speed
	 * @see MPU6050_RA_I2C_MST_CTRL
	 */
	void setMasterClockSpeed(int speed) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_I2C_MST_CTRL, MPU6050_I2C_MST_CLK_BIT, MPU6050_I2C_MST_CLK_LENGTH, speed);
	}

	// I2C_SLV* registers (Slave 0-3)

	/** Get the I2C address of the specified slave (0-3).
	 * Note that Bit 7 (MSB) controls read/write mode. If Bit 7 is set, it's a read
	 * operation, and if it is cleared, then it's a write operation. The remaining
	 * bits (6-0) are the 7-bit device address of the slave device.
	 *
	 * In read mode, the result of the read is placed in the lowest available 
	 * EXT_SENS_DATA register. For further information regarding the allocation of
	 * read results, please refer to the EXT_SENS_DATA register description
	 * (Registers 73 - 96).
	 *
	 * The MPU-6050 supports a total of five slaves, but Slave 4 has unique
	 * characteristics, and so it has its own functions (getSlave4* and setSlave4*).
	 *
	 * I2C data transactions are performed at the Sample Rate, as defined in
	 * Register 25. The user is responsible for ensuring that I2C data transactions
	 * to and from each enabled Slave can be completed within a single period of the
	 * Sample Rate.
	 *
	 * The I2C slave access rate can be reduced relative to the Sample Rate. This
	 * reduced access rate is determined by I2C_MST_DLY (Register 52). Whether a
	 * slave's access rate is reduced relative to the Sample Rate is determined by
	 * I2C_MST_DELAY_CTRL (Register 103).
	 *
	 * The processing order for the slaves is fixed. The sequence followed for
	 * processing the slaves is Slave 0, Slave 1, Slave 2, Slave 3 and Slave 4. If a
	 * particular Slave is disabled it will be skipped.
	 *
	 * Each slave can either be accessed at the sample rate or at a reduced sample
	 * rate. In a case where some slaves are accessed at the Sample Rate and some
	 * slaves are accessed at the reduced rate, the sequence of accessing the slaves
	 * (Slave 0 to Slave 4) is still followed. However, the reduced rate slaves will
	 * be skipped if their access rate dictates that they should not be accessed
	 * during that particular cycle. For further information regarding the reduced
	 * access rate, please refer to Register 52. Whether a slave is accessed at the
	 * Sample Rate or at the reduced rate is determined by the Delay Enable bits in
	 * Register 103.
	 *
	 * @param num Slave number (0-3)
	 * @return Current address for specified slave
	 * @see MPU6050_RA_I2C_SLV0_ADDR
	 */
	int getSlaveAddress(int num) {
	    if (num > 3) return 0;
	    I2CdevReadByte(deviceAddress, MPU6050_RA_I2C_SLV0_ADDR + num*3, bytebuffer);
	    return bytebuffer;
	}
	/** Set the I2C address of the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param address New address for specified slave
	 * @see getSlaveAddress()
	 * @see MPU6050_RA_I2C_SLV0_ADDR
	 */
	void setSlaveAddress(int num, int address) {
	    if (num > 3) return;
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_I2C_SLV0_ADDR + num*3, address);
	}
	/** Get the active internal register for the specified slave (0-3).
	 * Read/write operations for this slave will be done to whatever internal
	 * register address is stored in this MPU register.
	 *
	 * The MPU-6050 supports a total of five slaves, but Slave 4 has unique
	 * characteristics, and so it has its own functions.
	 *
	 * @param num Slave number (0-3)
	 * @return Current active register for specified slave
	 * @see MPU6050_RA_I2C_SLV0_REG
	 */
	int getSlaveRegister(int num) {
	    if (num > 3) return 0;
	    I2CdevReadByte(deviceAddress, MPU6050_RA_I2C_SLV0_REG + num*3, bytebuffer);
	    return bytebuffer;
	}
	/** Set the active internal register for the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param reg New active register for specified slave
	 * @see getSlaveRegister()
	 * @see MPU6050_RA_I2C_SLV0_REG
	 */
	void setSlaveRegister(int num, int reg) {
	    if (num > 3) return;
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_I2C_SLV0_REG + num*3, reg);
	}
	/** Get the enabled value for the specified slave (0-3).
	 * When set to 1, this bit enables Slave 0 for data transfer operations. When
	 * cleared to 0, this bit disables Slave 0 from data transfer operations.
	 * @param num Slave number (0-3)
	 * @return Current enabled value for specified slave
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	boolean getSlaveEnabled(int num) {
	    if (num > 3) return false;
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set the enabled value for the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param enabled New enabled value for specified slave
	 * @see getSlaveEnabled()
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	void setSlaveEnabled(int num, boolean enabled) {
	    if (num > 3) return;
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_EN_BIT, enabled);
	}
	/** Get word pair byte-swapping enabled for the specified slave (0-3).
	 * When set to 1, this bit enables byte swapping. When byte swapping is enabled,
	 * the high and low bytes of a word pair are swapped. Please refer to
	 * I2C_SLV0_GRP for the pairing convention of the word pairs. When cleared to 0,
	 * bytes transferred to and from Slave 0 will be written to EXT_SENS_DATA
	 * registers in the order they were transferred.
	 *
	 * @param num Slave number (0-3)
	 * @return Current word pair byte-swapping enabled value for specified slave
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	boolean getSlaveWordByteSwap(int num) {
	    if (num > 3) return false;
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_BYTE_SW_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set word pair byte-swapping enabled for the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param enabled New word pair byte-swapping enabled value for specified slave
	 * @see getSlaveWordByteSwap()
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	void setSlaveWordByteSwap(int num, boolean enabled) {
	    if (num > 3) return;
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_BYTE_SW_BIT, enabled);
	}
	/** Get write mode for the specified slave (0-3).
	 * When set to 1, the transaction will read or write data only. When cleared to
	 * 0, the transaction will write a register address prior to reading or writing
	 * data. This should equal 0 when specifying the register address within the
	 * Slave device to/from which the ensuing data transaction will take place.
	 *
	 * @param num Slave number (0-3)
	 * @return Current write mode for specified slave (0 = register address + data, 1 = data only)
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	boolean getSlaveWriteMode(int num) {
	    if (num > 3) return false;
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_REG_DIS_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set write mode for the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param mode New write mode for specified slave (0 = register address + data, 1 = data only)
	 * @see getSlaveWriteMode()
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	void setSlaveWriteMode(int num, boolean mode) {
	    if (num > 3) return;
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_REG_DIS_BIT, mode);
	}
	/** Get word pair grouping order offset for the specified slave (0-3).
	 * This sets specifies the grouping order of word pairs received from registers.
	 * When cleared to 0, bytes from register addresses 0 and 1, 2 and 3, etc (even,
	 * then odd register addresses) are paired to form a word. When set to 1, bytes
	 * from register addresses are paired 1 and 2, 3 and 4, etc. (odd, then even
	 * register addresses) are paired to form a word.
	 *
	 * @param num Slave number (0-3)
	 * @return Current word pair grouping order offset for specified slave
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	boolean getSlaveWordGroupOffset(int num) {
	    if (num > 3) return false;
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_GRP_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set word pair grouping order offset for the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param enabled New word pair grouping order offset for specified slave
	 * @see getSlaveWordGroupOffset()
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	void setSlaveWordGroupOffset(int num, boolean enabled) {
	    if (num > 3) return;
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_GRP_BIT, enabled);
	}
	/** Get number of bytes to read for the specified slave (0-3).
	 * Specifies the number of bytes transferred to and from Slave 0. Clearing this
	 * bit to 0 is equivalent to disabling the register by writing 0 to I2C_SLV0_EN.
	 * @param num Slave number (0-3)
	 * @return Number of bytes to read for specified slave
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	int getSlaveDataLength(int num) {
	    if (num > 3) return 0;
	    I2CdevReadBits(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_LEN_BIT, MPU6050_I2C_SLV_LEN_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set number of bytes to read for the specified slave (0-3).
	 * @param num Slave number (0-3)
	 * @param length Number of bytes to read for specified slave
	 * @see getSlaveDataLength()
	 * @see MPU6050_RA_I2C_SLV0_CTRL
	 */
	void setSlaveDataLength(int num, int length) {
	    if (num > 3) return;
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_I2C_SLV0_CTRL + num*3, MPU6050_I2C_SLV_LEN_BIT, MPU6050_I2C_SLV_LEN_LENGTH, length);
	}

	// I2C_SLV* registers (Slave 4)

	/** Get the I2C address of Slave 4.
	 * Note that Bit 7 (MSB) controls read/write mode. If Bit 7 is set, it's a read
	 * operation, and if it is cleared, then it's a write operation. The remaining
	 * bits (6-0) are the 7-bit device address of the slave device.
	 *
	 * @return Current address for Slave 4
	 * @see getSlaveAddress()
	 * @see MPU6050_RA_I2C_SLV4_ADDR
	 */
	int getSlave4Address() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_I2C_SLV4_ADDR, bytebuffer);
	    return bytebuffer;
	}
	/** Set the I2C address of Slave 4.
	 * @param address New address for Slave 4
	 * @see getSlave4Address()
	 * @see MPU6050_RA_I2C_SLV4_ADDR
	 */
	void setSlave4Address(int address) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_I2C_SLV4_ADDR, address);
	}
	/** Get the active internal register for the Slave 4.
	 * Read/write operations for this slave will be done to whatever internal
	 * register address is stored in this MPU register.
	 *
	 * @return Current active register for Slave 4
	 * @see MPU6050_RA_I2C_SLV4_REG
	 */
	int getSlave4Register() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_I2C_SLV4_REG, bytebuffer);
	    return bytebuffer;
	}
	/** Set the active internal register for Slave 4.
	 * @param reg New active register for Slave 4
	 * @see getSlave4Register()
	 * @see MPU6050_RA_I2C_SLV4_REG
	 */
	void setSlave4Register(int reg) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_I2C_SLV4_REG, reg);
	}
	/** Set new byte to write to Slave 4.
	 * This register stores the data to be written into the Slave 4. If I2C_SLV4_RW
	 * is set 1 (set to read), this register has no effect.
	 * @param data New byte to write to Slave 4
	 * @see MPU6050_RA_I2C_SLV4_DO
	 */
	void setSlave4OutputByte(int data) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_I2C_SLV4_DO, data);
	}
	/** Get the enabled value for the Slave 4.
	 * When set to 1, this bit enables Slave 4 for data transfer operations. When
	 * cleared to 0, this bit disables Slave 4 from data transfer operations.
	 * @return Current enabled value for Slave 4
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	boolean getSlave4Enabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set the enabled value for Slave 4.
	 * @param enabled New enabled value for Slave 4
	 * @see getSlave4Enabled()
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	void setSlave4Enabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_EN_BIT, enabled);
	}
	/** Get the enabled value for Slave 4 transaction interrupts.
	 * When set to 1, this bit enables the generation of an interrupt signal upon
	 * completion of a Slave 4 transaction. When cleared to 0, this bit disables the
	 * generation of an interrupt signal upon completion of a Slave 4 transaction.
	 * The interrupt status can be observed in Register 54.
	 *
	 * @return Current enabled value for Slave 4 transaction interrupts.
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	boolean getSlave4InterruptEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_INT_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set the enabled value for Slave 4 transaction interrupts.
	 * @param enabled New enabled value for Slave 4 transaction interrupts.
	 * @see getSlave4InterruptEnabled()
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	void setSlave4InterruptEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_INT_EN_BIT, enabled);
	}
	/** Get write mode for Slave 4.
	 * When set to 1, the transaction will read or write data only. When cleared to
	 * 0, the transaction will write a register address prior to reading or writing
	 * data. This should equal 0 when specifying the register address within the
	 * Slave device to/from which the ensuing data transaction will take place.
	 *
	 * @return Current write mode for Slave 4 (0 = register address + data, 1 = data only)
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	boolean getSlave4WriteMode() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_REG_DIS_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set write mode for the Slave 4.
	 * @param mode New write mode for Slave 4 (0 = register address + data, 1 = data only)
	 * @see getSlave4WriteMode()
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	void setSlave4WriteMode(boolean mode) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_REG_DIS_BIT, mode);
	}
	/** Get Slave 4 master delay value.
	 * This configures the reduced access rate of I2C slaves relative to the Sample
	 * Rate. When a slave's access rate is decreased relative to the Sample Rate,
	 * the slave is accessed every:
	 *
	 *     1 / (1 + I2C_MST_DLY) samples
	 *
	 * This base Sample Rate in turn is determined by SMPLRT_DIV (register 25) and
	 * DLPF_CFG (register 26). Whether a slave's access rate is reduced relative to
	 * the Sample Rate is determined by I2C_MST_DELAY_CTRL (register 103). For
	 * further information regarding the Sample Rate, please refer to register 25.
	 *
	 * @return Current Slave 4 master delay value
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	int getSlave4MasterDelay() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_MST_DLY_BIT, MPU6050_I2C_SLV4_MST_DLY_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set Slave 4 master delay value.
	 * @param delay New Slave 4 master delay value
	 * @see getSlave4MasterDelay()
	 * @see MPU6050_RA_I2C_SLV4_CTRL
	 */
	void setSlave4MasterDelay(int delay) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_I2C_SLV4_CTRL, MPU6050_I2C_SLV4_MST_DLY_BIT, MPU6050_I2C_SLV4_MST_DLY_LENGTH, delay);
	}
	/** Get last available byte read from Slave 4.
	 * This register stores the data read from Slave 4. This field is populated
	 * after a read transaction.
	 * @return Last available byte read from to Slave 4
	 * @see MPU6050_RA_I2C_SLV4_DI
	 */
	int getSlate4InputByte() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_I2C_SLV4_DI, bytebuffer);
	    return bytebuffer;
	}

	// I2C_MST_STATUS register

	/** Get FSYNC interrupt status.
	 * This bit reflects the status of the FSYNC interrupt from an external device
	 * into the MPU-60X0. This is used as a way to pass an external interrupt
	 * through the MPU-60X0 to the host application processor. When set to 1, this
	 * bit will cause an interrupt if FSYNC_INT_EN is asserted in INT_PIN_CFG
	 * (Register 55).
	 * @return FSYNC interrupt status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getPassthroughStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_PASS_THROUGH_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Slave 4 transaction done status.
	 * Automatically sets to 1 when a Slave 4 transaction has completed. This
	 * triggers an interrupt if the I2C_MST_INT_EN bit in the INT_ENABLE register
	 * (Register 56) is asserted and if the SLV_4_DONE_INT bit is asserted in the
	 * I2C_SLV4_CTRL register (Register 52).
	 * @return Slave 4 transaction done status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getSlave4IsDone() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_SLV4_DONE_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get master arbitration lost status.
	 * This bit automatically sets to 1 when the I2C Master has lost arbitration of
	 * the auxiliary I2C bus (an error condition). This triggers an interrupt if the
	 * I2C_MST_INT_EN bit in the INT_ENABLE register (Register 56) is asserted.
	 * @return Master arbitration lost status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getLostArbitration() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_LOST_ARB_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Slave 4 NACK status.
	 * This bit automatically sets to 1 when the I2C Master receives a NACK in a
	 * transaction with Slave 4. This triggers an interrupt if the I2C_MST_INT_EN
	 * bit in the INT_ENABLE register (Register 56) is asserted.
	 * @return Slave 4 NACK interrupt status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getSlave4Nack() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_SLV4_NACK_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Slave 3 NACK status.
	 * This bit automatically sets to 1 when the I2C Master receives a NACK in a
	 * transaction with Slave 3. This triggers an interrupt if the I2C_MST_INT_EN
	 * bit in the INT_ENABLE register (Register 56) is asserted.
	 * @return Slave 3 NACK interrupt status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getSlave3Nack() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_SLV3_NACK_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Slave 2 NACK status.
	 * This bit automatically sets to 1 when the I2C Master receives a NACK in a
	 * transaction with Slave 2. This triggers an interrupt if the I2C_MST_INT_EN
	 * bit in the INT_ENABLE register (Register 56) is asserted.
	 * @return Slave 2 NACK interrupt status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getSlave2Nack() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_SLV2_NACK_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Slave 1 NACK status.
	 * This bit automatically sets to 1 when the I2C Master receives a NACK in a
	 * transaction with Slave 1. This triggers an interrupt if the I2C_MST_INT_EN
	 * bit in the INT_ENABLE register (Register 56) is asserted.
	 * @return Slave 1 NACK interrupt status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getSlave1Nack() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_SLV1_NACK_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Slave 0 NACK status.
	 * This bit automatically sets to 1 when the I2C Master receives a NACK in a
	 * transaction with Slave 0. This triggers an interrupt if the I2C_MST_INT_EN
	 * bit in the INT_ENABLE register (Register 56) is asserted.
	 * @return Slave 0 NACK interrupt status
	 * @see MPU6050_RA_I2C_MST_STATUS
	 */
	boolean getSlave0Nack() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_STATUS, MPU6050_MST_I2C_SLV0_NACK_BIT, bytebuffer);
	    return bytebuffer != 0;
	}

	// INT_PIN_CFG register

	/** Get interrupt logic level mode.
	 * Will be set 0 for active-high, 1 for active-low.
	 * @return Current interrupt mode (0=active-high, 1=active-low)
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_INT_LEVEL_BIT
	 */
	boolean getInterruptMode() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_INT_LEVEL_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set interrupt logic level mode.
	 * @param mode New interrupt mode (0=active-high, 1=active-low)
	 * @see getInterruptMode()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_INT_LEVEL_BIT
	 */
	void setInterruptMode(boolean mode) {
	   I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_INT_LEVEL_BIT, mode);
	}
	/** Get interrupt drive mode.
	 * Will be set 0 for push-pull, 1 for open-drain.
	 * @return Current interrupt drive mode (0=push-pull, 1=open-drain)
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_INT_OPEN_BIT
	 */
	boolean getInterruptDrive() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_INT_OPEN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set interrupt drive mode.
	 * @param drive New interrupt drive mode (0=push-pull, 1=open-drain)
	 * @see getInterruptDrive()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_INT_OPEN_BIT
	 */
	void setInterruptDrive(boolean drive) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_INT_OPEN_BIT, drive);
	}
	/** Get interrupt latch mode.
	 * Will be set 0 for 50us-pulse, 1 for latch-until-int-cleared.
	 * @return Current latch mode (0=50us-pulse, 1=latch-until-int-cleared)
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_LATCH_INT_EN_BIT
	 */
	boolean getInterruptLatch() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_LATCH_INT_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set interrupt latch mode.
	 * @param latch New latch mode (0=50us-pulse, 1=latch-until-int-cleared)
	 * @see getInterruptLatch()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_LATCH_INT_EN_BIT
	 */
	void setInterruptLatch(boolean latch) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_LATCH_INT_EN_BIT, latch);
	}
	/** Get interrupt latch clear mode.
	 * Will be set 0 for status-read-only, 1 for any-register-read.
	 * @return Current latch clear mode (0=status-read-only, 1=any-register-read)
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_INT_RD_CLEAR_BIT
	 */
	boolean getInterruptLatchClear() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_INT_RD_CLEAR_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set interrupt latch clear mode.
	 * @param clear New latch clear mode (0=status-read-only, 1=any-register-read)
	 * @see getInterruptLatchClear()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_INT_RD_CLEAR_BIT
	 */
	void setInterruptLatchClear(boolean clear) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_INT_RD_CLEAR_BIT, clear);
	}
	/** Get FSYNC interrupt logic level mode.
	 * @return Current FSYNC interrupt mode (0=active-high, 1=active-low)
	 * @see getFSyncInterruptMode()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_FSYNC_INT_LEVEL_BIT
	 */
	boolean getFSyncInterruptLevel() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_FSYNC_INT_LEVEL_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set FSYNC interrupt logic level mode.
	 * @param mode New FSYNC interrupt mode (0=active-high, 1=active-low)
	 * @see getFSyncInterruptMode()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_FSYNC_INT_LEVEL_BIT
	 */
	void setFSyncInterruptLevel(boolean level) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_FSYNC_INT_LEVEL_BIT, level);
	}
	/** Get FSYNC pin interrupt enabled setting.
	 * Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled setting
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_FSYNC_INT_EN_BIT
	 */
	boolean getFSyncInterruptEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_FSYNC_INT_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set FSYNC pin interrupt enabled setting.
	 * @param enabled New FSYNC pin interrupt enabled setting
	 * @see getFSyncInterruptEnabled()
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_FSYNC_INT_EN_BIT
	 */
	void setFSyncInterruptEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_FSYNC_INT_EN_BIT, enabled);
	}
	/** Get I2C bypass enabled status.
	 * When this bit is equal to 1 and I2C_MST_EN (Register 106 bit[5]) is equal to
	 * 0, the host application processor will be able to directly access the
	 * auxiliary I2C bus of the MPU-60X0. When this bit is equal to 0, the host
	 * application processor will not be able to directly access the auxiliary I2C
	 * bus of the MPU-60X0 regardless of the state of I2C_MST_EN (Register 106
	 * bit[5]).
	 * @return Current I2C bypass enabled status
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_I2C_BYPASS_EN_BIT
	 */
	boolean getI2CBypassEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_I2C_BYPASS_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set I2C bypass enabled status.
	 * When this bit is equal to 1 and I2C_MST_EN (Register 106 bit[5]) is equal to
	 * 0, the host application processor will be able to directly access the
	 * auxiliary I2C bus of the MPU-60X0. When this bit is equal to 0, the host
	 * application processor will not be able to directly access the auxiliary I2C
	 * bus of the MPU-60X0 regardless of the state of I2C_MST_EN (Register 106
	 * bit[5]).
	 * @param enabled New I2C bypass enabled status
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_I2C_BYPASS_EN_BIT
	 */
	void setI2CBypassEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_I2C_BYPASS_EN_BIT, enabled);
	}
	/** Get reference clock output enabled status.
	 * When this bit is equal to 1, a reference clock output is provided at the
	 * CLKOUT pin. When this bit is equal to 0, the clock output is disabled. For
	 * further information regarding CLKOUT, please refer to the MPU-60X0 Product
	 * Specification document.
	 * @return Current reference clock output enabled status
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_CLKOUT_EN_BIT
	 */
	boolean getClockOutputEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_CLKOUT_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set reference clock output enabled status.
	 * When this bit is equal to 1, a reference clock output is provided at the
	 * CLKOUT pin. When this bit is equal to 0, the clock output is disabled. For
	 * further information regarding CLKOUT, please refer to the MPU-60X0 Product
	 * Specification document.
	 * @param enabled New reference clock output enabled status
	 * @see MPU6050_RA_INT_PIN_CFG
	 * @see MPU6050_INTCFG_CLKOUT_EN_BIT
	 */
	void setClockOutputEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_PIN_CFG, MPU6050_INTCFG_CLKOUT_EN_BIT, enabled);
	}

	// INT_ENABLE register

	/** Get full interrupt enabled status.
	 * Full register byte for all interrupts, for quick reading. Each bit will be
	 * set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_FF_BIT
	 **/
	int getIntEnabled() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_INT_ENABLE, bytebuffer);
	    return bytebuffer;
	}
	/** Set full interrupt enabled status.
	 * Full register byte for all interrupts, for quick reading. Each bit should be
	 * set 0 for disabled, 1 for enabled.
	 * @param enabled New interrupt enabled status
	 * @see getIntFreefallEnabled()
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_FF_BIT
	 **/
	void setIntEnabled(int enabled) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_INT_ENABLE, enabled);
	}
	/** Get Free Fall interrupt enabled status.
	 * Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_FF_BIT
	 **/
	boolean getIntFreefallEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_FF_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Free Fall interrupt enabled status.
	 * @param enabled New interrupt enabled status
	 * @see getIntFreefallEnabled()
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_FF_BIT
	 **/
	void setIntFreefallEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_FF_BIT, enabled);
	}
	/** Get Motion Detection interrupt enabled status.
	 * Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_MOT_BIT
	 **/
	boolean getIntMotionEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_MOT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Motion Detection interrupt enabled status.
	 * @param enabled New interrupt enabled status
	 * @see getIntMotionEnabled()
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_MOT_BIT
	 **/
	void setIntMotionEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_MOT_BIT, enabled);
	}
	/** Get Zero Motion Detection interrupt enabled status.
	 * Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_ZMOT_BIT
	 **/
	boolean getIntZeroMotionEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_ZMOT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Zero Motion Detection interrupt enabled status.
	 * @param enabled New interrupt enabled status
	 * @see getIntZeroMotionEnabled()
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_ZMOT_BIT
	 **/
	void setIntZeroMotionEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_ZMOT_BIT, enabled);
	}
	/** Get FIFO Buffer Overflow interrupt enabled status.
	 * Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_FIFO_OFLOW_BIT
	 **/
	boolean getIntFIFOBufferOverflowEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_FIFO_OFLOW_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set FIFO Buffer Overflow interrupt enabled status.
	 * @param enabled New interrupt enabled status
	 * @see getIntFIFOBufferOverflowEnabled()
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_FIFO_OFLOW_BIT
	 **/
	void setIntFIFOBufferOverflowEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_FIFO_OFLOW_BIT, enabled);
	}
	/** Get I2C Master interrupt enabled status.
	 * This enables any of the I2C Master interrupt sources to generate an
	 * interrupt. Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_I2C_MST_INT_BIT
	 **/
	boolean getIntI2CMasterEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_I2C_MST_INT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set I2C Master interrupt enabled status.
	 * @param enabled New interrupt enabled status
	 * @see getIntI2CMasterEnabled()
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_I2C_MST_INT_BIT
	 **/
	void setIntI2CMasterEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_I2C_MST_INT_BIT, enabled);
	}
	/** Get Data Ready interrupt enabled setting.
	 * This event occurs each time a write operation to all of the sensor registers
	 * has been completed. Will be set 0 for disabled, 1 for enabled.
	 * @return Current interrupt enabled status
	 * @see MPU6050_RA_INT_ENABLE
	 * @see MPU6050_INTERRUPT_DATA_RDY_BIT
	 */
	boolean getIntDataReadyEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_DATA_RDY_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Data Ready interrupt enabled status.
	 * @param enabled New interrupt enabled status
	 * @see getIntDataReadyEnabled()
	 * @see MPU6050_RA_INT_CFG
	 * @see MPU6050_INTERRUPT_DATA_RDY_BIT
	 */
	void setIntDataReadyEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_DATA_RDY_BIT, enabled);
	}

	// INT_STATUS register

	/** Get full set of interrupt status bits.
	 * These bits clear to 0 after the register has been read. Very useful
	 * for getting multiple INT statuses, since each single bit read clears
	 * all of them because it has to read the whole byte.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 */
	int getIntStatus() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_INT_STATUS, bytebuffer);
	    return bytebuffer;
	}
	/** Get Free Fall interrupt status.
	 * This bit automatically sets to 1 when a Free Fall interrupt has been
	 * generated. The bit clears to 0 after the register has been read.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 * @see MPU6050_INTERRUPT_FF_BIT
	 */
	boolean getIntFreefallStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_FF_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	 /** Get Motion Detection interrupt status.
	 * This bit automatically sets to 1 when a Motion Detection interrupt has been
	 * generated. The bit clears to 0 after the register has been read.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 * @see MPU6050_INTERRUPT_MOT_BIT
	 */
	boolean getIntMotionStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_MOT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Zero Motion Detection interrupt status.
	 * This bit automatically sets to 1 when a Zero Motion Detection interrupt has
	 * been generated. The bit clears to 0 after the register has been read.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 * @see MPU6050_INTERRUPT_ZMOT_BIT
	 */
	boolean getIntZeroMotionStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_ZMOT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get FIFO Buffer Overflow interrupt status.
	 * This bit automatically sets to 1 when a Free Fall interrupt has been
	 * generated. The bit clears to 0 after the register has been read.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 * @see MPU6050_INTERRUPT_FIFO_OFLOW_BIT
	 */
	boolean getIntFIFOBufferOverflowStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_FIFO_OFLOW_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get I2C Master interrupt status.
	 * This bit automatically sets to 1 when an I2C Master interrupt has been
	 * generated. For a list of I2C Master interrupts, please refer to Register 54.
	 * The bit clears to 0 after the register has been read.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 * @see MPU6050_INTERRUPT_I2C_MST_INT_BIT
	 */
	boolean getIntI2CMasterStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_I2C_MST_INT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Data Ready interrupt status.
	 * This bit automatically sets to 1 when a Data Ready interrupt has been
	 * generated. The bit clears to 0 after the register has been read.
	 * @return Current interrupt status
	 * @see MPU6050_RA_INT_STATUS
	 * @see MPU6050_INTERRUPT_DATA_RDY_BIT
	 */
	boolean getIntDataReadyStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_DATA_RDY_BIT, bytebuffer);
	    return bytebuffer != 0;
	}

	// ACCEL_*OUT_* registers

	/** Get raw 9-axis motion sensor readings (accel/gyro/compass).
	 * FUNCTION NOT FULLY IMPLEMENTED YET.
	 * @param ax 16-bit signed integer container for accelerometer X-axis value
	 * @param ay 16-bit signed integer container for accelerometer Y-axis value
	 * @param az 16-bit signed integer container for accelerometer Z-axis value
	 * @param gx 16-bit signed integer container for gyroscope X-axis value
	 * @param gy 16-bit signed integer container for gyroscope Y-axis value
	 * @param gz 16-bit signed integer container for gyroscope Z-axis value
	 * @param mx 16-bit signed integer container for magnetometer X-axis value
	 * @param my 16-bit signed integer container for magnetometer Y-axis value
	 * @param mz 16-bit signed integer container for magnetometer Z-axis value
	 * @see getMotion6()
	 * @see getAcceleration()
	 * @see getRotation()
	 * @see MPU6050_RA_ACCEL_XOUT_H
	 */
	void getMotion9(int ax, int ay, int az, int gx, int gy, int gz, int mx, int my, int mz) {
	    getMotion6(ax, ay, az, gx, gy, gz);
	    // TODO: magnetometer integration
	}
	/** Get raw 6-axis motion sensor readings (accel/gyro).
	 * Retrieves all currently available motion sensor values.
	 * @param ax 16-bit signed integer container for accelerometer X-axis value
	 * @param ay 16-bit signed integer container for accelerometer Y-axis value
	 * @param az 16-bit signed integer container for accelerometer Z-axis value
	 * @param gx 16-bit signed integer container for gyroscope X-axis value
	 * @param gy 16-bit signed integer container for gyroscope Y-axis value
	 * @param gz 16-bit signed integer container for gyroscope Z-axis value
	 * @see getAcceleration()
	 * @see getRotation()
	 * @see MPU6050_RA_ACCEL_XOUT_H
	 */
	void getMotion6(int ax, int ay, int az, int gx, int gy, int gz) {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ACCEL_XOUT_H, 14, buffer);
	    ax = (((int)buffer[0]) << 8) | buffer[1];
	    ay = (((int)buffer[2]) << 8) | buffer[3];
	    az = (((int)buffer[4]) << 8) | buffer[5];
	    gx = (((int)buffer[8]) << 8) | buffer[9];
	    gy = (((int)buffer[10]) << 8) | buffer[11];
	    gz = (((int)buffer[12]) << 8) | buffer[13];
	}
	/** Get 3-axis accelerometer readings.
	 * These registers store the most recent accelerometer measurements.
	 * Accelerometer measurements are written to these registers at the Sample Rate
	 * as defined in Register 25.
	 *
	 * The accelerometer measurement registers, along with the temperature
	 * measurement registers, gyroscope measurement registers, and external sensor
	 * data registers, are composed of two sets of registers: an internal register
	 * set and a user-facing read register set.
	 *
	 * The data within the accelerometer sensors' internal register set is always
	 * updated at the Sample Rate. Meanwhile, the user-facing read register set
	 * duplicates the internal register set's data values whenever the serial
	 * interface is idle. This guarantees that a burst read of sensor registers will
	 * read measurements from the same sampling instant. Note that if burst reads
	 * are not used, the user is responsible for ensuring a set of single byte reads
	 * correspond to a single sampling instant by checking the Data Ready interrupt.
	 *
	 * Each 16-bit accelerometer measurement has a full scale defined in ACCEL_FS
	 * (Register 28). For each full scale setting, the accelerometers' sensitivity
	 * per LSB in ACCEL_xOUT is shown in the table below:
	 *
	 * <pre>
	 * AFS_SEL | Full Scale Range | LSB Sensitivity
	 * --------+------------------+----------------
	 * 0       | +/- 2g           | 8192 LSB/mg
	 * 1       | +/- 4g           | 4096 LSB/mg
	 * 2       | +/- 8g           | 2048 LSB/mg
	 * 3       | +/- 16g          | 1024 LSB/mg
	 * </pre>
	 *
	 * @param x 16-bit signed integer container for X-axis acceleration
	 * @param y 16-bit signed integer container for Y-axis acceleration
	 * @param z 16-bit signed integer container for Z-axis acceleration
	 * @see MPU6050_RA_GYRO_XOUT_H
	 */
	void getAcceleration(int x, int y, int z) {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ACCEL_XOUT_H, 6, buffer);
	    x = (((int)buffer[0]) << 8) | buffer[1];
	    y = (((int)buffer[2]) << 8) | buffer[3];
	    z = (((int)buffer[4]) << 8) | buffer[5];
	}
	/** Get X-axis accelerometer reading.
	 * @return X-axis acceleration measurement in 16-bit 2's complement format
	 * @see getMotion6()
	 * @see MPU6050_RA_ACCEL_XOUT_H
	 */
	int getAccelerationX() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ACCEL_XOUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	/** Get Y-axis accelerometer reading.
	 * @return Y-axis acceleration measurement in 16-bit 2's complement format
	 * @see getMotion6()
	 * @see MPU6050_RA_ACCEL_YOUT_H
	 */
	int getAccelerationY() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ACCEL_YOUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	/** Get Z-axis accelerometer reading.
	 * @return Z-axis acceleration measurement in 16-bit 2's complement format
	 * @see getMotion6()
	 * @see MPU6050_RA_ACCEL_ZOUT_H
	 */
	int getAccelerationZ() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ACCEL_ZOUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}

	// TEMP_OUT_* registers

	/** Get current internal temperature.
	 * @return Temperature reading in 16-bit 2's complement format
	 * @see MPU6050_RA_TEMP_OUT_H
	 */
	int getTemperature() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_TEMP_OUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}

	// GYRO_*OUT_* registers

	/** Get 3-axis gyroscope readings.
	 * These gyroscope measurement registers, along with the accelerometer
	 * measurement registers, temperature measurement registers, and external sensor
	 * data registers, are composed of two sets of registers: an internal register
	 * set and a user-facing read register set.
	 * The data within the gyroscope sensors' internal register set is always
	 * updated at the Sample Rate. Meanwhile, the user-facing read register set
	 * duplicates the internal register set's data values whenever the serial
	 * interface is idle. This guarantees that a burst read of sensor registers will
	 * read measurements from the same sampling instant. Note that if burst reads
	 * are not used, the user is responsible for ensuring a set of single byte reads
	 * correspond to a single sampling instant by checking the Data Ready interrupt.
	 *
	 * Each 16-bit gyroscope measurement has a full scale defined in FS_SEL
	 * (Register 27). For each full scale setting, the gyroscopes' sensitivity per
	 * LSB in GYRO_xOUT is shown in the table below:
	 *
	 * <pre>
	 * FS_SEL | Full Scale Range   | LSB Sensitivity
	 * -------+--------------------+----------------
	 * 0      | +/- 250 degrees/s  | 131 LSB/deg/s
	 * 1      | +/- 500 degrees/s  | 65.5 LSB/deg/s
	 * 2      | +/- 1000 degrees/s | 32.8 LSB/deg/s
	 * 3      | +/- 2000 degrees/s | 16.4 LSB/deg/s
	 * </pre>
	 *
	 * @param x 16-bit signed integer container for X-axis rotation
	 * @param y 16-bit signed integer container for Y-axis rotation
	 * @param z 16-bit signed integer container for Z-axis rotation
	 * @see getMotion6()
	 * @see MPU6050_RA_GYRO_XOUT_H
	 */
	void getRotation(int x, int y, int z) {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_GYRO_XOUT_H, 6, buffer);
	    x = (((int)buffer[0]) << 8) | buffer[1];
	    y = (((int)buffer[2]) << 8) | buffer[3];
	    z = (((int)buffer[4]) << 8) | buffer[5];
	}
	/** Get X-axis gyroscope reading.
	 * @return X-axis rotation measurement in 16-bit 2's complement format
	 * @see getMotion6()
	 * @see MPU6050_RA_GYRO_XOUT_H
	 */
	int getRotationX() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_GYRO_XOUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	/** Get Y-axis gyroscope reading.
	 * @return Y-axis rotation measurement in 16-bit 2's complement format
	 * @see getMotion6()
	 * @see MPU6050_RA_GYRO_YOUT_H
	 */
	int getRotationY() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_GYRO_YOUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	/** Get Z-axis gyroscope reading.
	 * @return Z-axis rotation measurement in 16-bit 2's complement format
	 * @see getMotion6()
	 * @see MPU6050_RA_GYRO_ZOUT_H
	 */
	int getRotationZ() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_GYRO_ZOUT_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}

	// EXT_SENS_DATA_* registers

	/** Read single byte from external sensor data register.
	 * These registers store data read from external sensors by the Slave 0, 1, 2,
	 * and 3 on the auxiliary I2C interface. Data read by Slave 4 is stored in
	 * I2C_SLV4_DI (Register 53).
	 *
	 * External sensor data is written to these registers at the Sample Rate as
	 * defined in Register 25. This access rate can be reduced by using the Slave
	 * Delay Enable registers (Register 103).
	 *
	 * External sensor data registers, along with the gyroscope measurement
	 * registers, accelerometer measurement registers, and temperature measurement
	 * registers, are composed of two sets of registers: an internal register set
	 * and a user-facing read register set.
	 *
	 * The data within the external sensors' internal register set is always updated
	 * at the Sample Rate (or the reduced access rate) whenever the serial interface
	 * is idle. This guarantees that a burst read of sensor registers will read
	 * measurements from the same sampling instant. Note that if burst reads are not
	 * used, the user is responsible for ensuring a set of single byte reads
	 * correspond to a single sampling instant by checking the Data Ready interrupt.
	 *
	 * Data is placed in these external sensor data registers according to
	 * I2C_SLV0_CTRL, I2C_SLV1_CTRL, I2C_SLV2_CTRL, and I2C_SLV3_CTRL (Registers 39,
	 * 42, 45, and 48). When more than zero bytes are read (I2C_SLVx_LEN > 0) from
	 * an enabled slave (I2C_SLVx_EN = 1), the slave is read at the Sample Rate (as
	 * defined in Register 25) or delayed rate (if specified in Register 52 and
	 * 103). During each Sample cycle, slave reads are performed in order of Slave
	 * number. If all slaves are enabled with more than zero bytes to be read, the
	 * order will be Slave 0, followed by Slave 1, Slave 2, and Slave 3.
	 *
	 * Each enabled slave will have EXT_SENS_DATA registers associated with it by
	 * number of bytes read (I2C_SLVx_LEN) in order of slave number, starting from
	 * EXT_SENS_DATA_00. Note that this means enabling or disabling a slave may
	 * change the higher numbered slaves' associated registers. Furthermore, if
	 * fewer total bytes are being read from the external sensors as a result of
	 * such a change, then the data remaining in the registers which no longer have
	 * an associated slave device (i.e. high numbered registers) will remain in
	 * these previously allocated registers unless reset.
	 *
	 * If the sum of the read lengths of all SLVx transactions exceed the number of
	 * available EXT_SENS_DATA registers, the excess bytes will be dropped. There
	 * are 24 EXT_SENS_DATA registers and hence the total read lengths between all
	 * the slaves cannot be greater than 24 or some bytes will be lost.
	 *
	 * Note: Slave 4's behavior is distinct from that of Slaves 0-3. For further
	 * information regarding the characteristics of Slave 4, please refer to
	 * Registers 49 to 53.
	 *
	 * EXAMPLE:
	 * Suppose that Slave 0 is enabled with 4 bytes to be read (I2C_SLV0_EN = 1 and
	 * I2C_SLV0_LEN = 4) while Slave 1 is enabled with 2 bytes to be read so that
	 * I2C_SLV1_EN = 1 and I2C_SLV1_LEN = 2. In such a situation, EXT_SENS_DATA _00
	 * through _03 will be associated with Slave 0, while EXT_SENS_DATA _04 and 05
	 * will be associated with Slave 1. If Slave 2 is enabled as well, registers
	 * starting from EXT_SENS_DATA_06 will be allocated to Slave 2.
	 *
	 * If Slave 2 is disabled while Slave 3 is enabled in this same situation, then
	 * registers starting from EXT_SENS_DATA_06 will be allocated to Slave 3
	 * instead.
	 *
	 * REGISTER ALLOCATION FOR DYNAMIC DISABLE VS. NORMAL DISABLE:
	 * If a slave is disabled at any time, the space initially allocated to the
	 * slave in the EXT_SENS_DATA register, will remain associated with that slave.
	 * This is to avoid dynamic adjustment of the register allocation.
	 *
	 * The allocation of the EXT_SENS_DATA registers is recomputed only when (1) all
	 * slaves are disabled, or (2) the I2C_MST_RST bit is set (Register 106).
	 *
	 * This above is also true if one of the slaves gets NACKed and stops
	 * functioning.
	 *
	 * @param position Starting position (0-23)
	 * @return Byte read from register
	 */
	int getExternalSensorByte(int position) {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_EXT_SENS_DATA_00 + position, bytebuffer);
	    return bytebuffer;
	}
	/** Read word (2 bytes) from external sensor data registers.
	 * @param position Starting position (0-21)
	 * @return Word read from register
	 * @see getExternalSensorByte()
	 */
	int getExternalSensorWord(int position) {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_EXT_SENS_DATA_00 + position, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	/** Read double word (4 bytes) from external sensor data registers.
	 * @param position Starting position (0-20)
	 * @return Double word read from registers
	 * @see getExternalSensorByte()
	 */
	int getExternalSensorDWord(int position) {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_EXT_SENS_DATA_00 + position, 4, buffer);
	    return (((int)buffer[0]) << 24) | (((int)buffer[1]) << 16) | (((int)buffer[2]) << 8) | buffer[3];
	}

	// MOT_DETECT_STATUS register

	/** Get full motion detection status register content (all bits).
	 * @return Motion detection status byte
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 */
	int getMotionStatus() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, bytebuffer);
	    return bytebuffer;
	}
	/** Get X-axis negative motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_XNEG_BIT
	 */
	boolean getXNegMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_XNEG_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get X-axis positive motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_XPOS_BIT
	 */
	boolean getXPosMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_XPOS_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Y-axis negative motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_YNEG_BIT
	 */
	boolean getYNegMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_YNEG_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Y-axis positive motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_YPOS_BIT
	 */
	boolean getYPosMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_YPOS_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Z-axis negative motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_ZNEG_BIT
	 */
	boolean getZNegMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_ZNEG_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get Z-axis positive motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_ZPOS_BIT
	 */
	boolean getZPosMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_ZPOS_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Get zero motion detection interrupt status.
	 * @return Motion detection status
	 * @see MPU6050_RA_MOT_DETECT_STATUS
	 * @see MPU6050_MOTION_MOT_ZRMOT_BIT
	 */
	boolean getZeroMotionDetected() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_MOT_DETECT_STATUS, MPU6050_MOTION_MOT_ZRMOT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}

	// I2C_SLV*_DO register

	/** Write byte to Data Output container for specified slave.
	 * This register holds the output data written into Slave when Slave is set to
	 * write mode. For further information regarding Slave control, please
	 * refer to Registers 37 to 39 and immediately following.
	 * @param num Slave number (0-3)
	 * @param data Byte to write
	 * @see MPU6050_RA_I2C_SLV0_DO
	 */
	void setSlaveOutputByte(int num, int data) {
	    if (num > 3) return;
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_I2C_SLV0_DO + num, data);
	}

	// I2C_MST_DELAY_CTRL register

	/** Get external data shadow delay enabled status.
	 * This register is used to specify the timing of external sensor data
	 * shadowing. When DELAY_ES_SHADOW is set to 1, shadowing of external
	 * sensor data is delayed until all data has been received.
	 * @return Current external data shadow delay enabled status.
	 * @see MPU6050_RA_I2C_MST_DELAY_CTRL
	 * @see MPU6050_DELAYCTRL_DELAY_ES_SHADOW_BIT
	 */
	boolean getExternalShadowDelayEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_DELAY_CTRL, MPU6050_DELAYCTRL_DELAY_ES_SHADOW_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set external data shadow delay enabled status.
	 * @param enabled New external data shadow delay enabled status.
	 * @see getExternalShadowDelayEnabled()
	 * @see MPU6050_RA_I2C_MST_DELAY_CTRL
	 * @see MPU6050_DELAYCTRL_DELAY_ES_SHADOW_BIT
	 */
	void setExternalShadowDelayEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_MST_DELAY_CTRL, MPU6050_DELAYCTRL_DELAY_ES_SHADOW_BIT, enabled);
	}
	/** Get slave delay enabled status.
	 * When a particular slave delay is enabled, the rate of access for the that
	 * slave device is reduced. When a slave's access rate is decreased relative to
	 * the Sample Rate, the slave is accessed every:
	 *
	 *     1 / (1 + I2C_MST_DLY) Samples
	 *
	 * This base Sample Rate in turn is determined by SMPLRT_DIV (register  * 25)
	 * and DLPF_CFG (register 26).
	 *
	 * For further information regarding I2C_MST_DLY, please refer to register 52.
	 * For further information regarding the Sample Rate, please refer to register 25.
	 *
	 * @param num Slave number (0-4)
	 * @return Current slave delay enabled status.
	 * @see MPU6050_RA_I2C_MST_DELAY_CTRL
	 * @see MPU6050_DELAYCTRL_I2C_SLV0_DLY_EN_BIT
	 */
	boolean getSlaveDelayEnabled(int num) {
	    // MPU6050_DELAYCTRL_I2C_SLV4_DLY_EN_BIT is 4, SLV3 is 3, etc.
	    if (num > 4) return false;
	    I2CdevReadBit(deviceAddress, MPU6050_RA_I2C_MST_DELAY_CTRL, num, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set slave delay enabled status.
	 * @param num Slave number (0-4)
	 * @param enabled New slave delay enabled status.
	 * @see MPU6050_RA_I2C_MST_DELAY_CTRL
	 * @see MPU6050_DELAYCTRL_I2C_SLV0_DLY_EN_BIT
	 */
	void setSlaveDelayEnabled(int num, boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_I2C_MST_DELAY_CTRL, num, enabled);
	}

	// SIGNAL_PATH_RESET register

	/** Reset gyroscope signal path.
	 * The reset will revert the signal path analog to digital converters and
	 * filters to their power up configurations.
	 * @see MPU6050_RA_SIGNAL_PATH_RESET
	 * @see MPU6050_PATHRESET_GYRO_RESET_BIT
	 */
	void resetGyroscopePath() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_SIGNAL_PATH_RESET, MPU6050_PATHRESET_GYRO_RESET_BIT, true);
	}
	/** Reset accelerometer signal path.
	 * The reset will revert the signal path analog to digital converters and
	 * filters to their power up configurations.
	 * @see MPU6050_RA_SIGNAL_PATH_RESET
	 * @see MPU6050_PATHRESET_ACCEL_RESET_BIT
	 */
	void resetAccelerometerPath() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_SIGNAL_PATH_RESET, MPU6050_PATHRESET_ACCEL_RESET_BIT, true);
	}
	/** Reset temperature sensor signal path.
	 * The reset will revert the signal path analog to digital converters and
	 * filters to their power up configurations.
	 * @see MPU6050_RA_SIGNAL_PATH_RESET
	 * @see MPU6050_PATHRESET_TEMP_RESET_BIT
	 */
	void resetTemperaturePath() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_SIGNAL_PATH_RESET, MPU6050_PATHRESET_TEMP_RESET_BIT, true);
	}

	// MOT_DETECT_CTRL register

	/** Get accelerometer power-on delay.
	 * The accelerometer data path provides samples to the sensor registers, Motion
	 * detection, Zero Motion detection, and Free Fall detection modules. The
	 * signal path contains filters which must be flushed on wake-up with new
	 * samples before the detection modules begin operations. The default wake-up
	 * delay, of 4ms can be lengthened by up to 3ms. This additional delay is
	 * specified in ACCEL_ON_DELAY in units of 1 LSB = 1 ms. The user may select
	 * any value above zero unless instructed otherwise by InvenSense. Please refer
	 * to Section 8 of the MPU-6000/MPU-6050 Product Specification document for
	 * further information regarding the detection modules.
	 * @return Current accelerometer power-on delay
	 * @see MPU6050_RA_MOT_DETECT_CTRL
	 * @see MPU6050_DETECT_ACCEL_ON_DELAY_BIT
	 */
	int getAccelerometerPowerOnDelay() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_MOT_DETECT_CTRL, MPU6050_DETECT_ACCEL_ON_DELAY_BIT, MPU6050_DETECT_ACCEL_ON_DELAY_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set accelerometer power-on delay.
	 * @param delay New accelerometer power-on delay (0-3)
	 * @see getAccelerometerPowerOnDelay()
	 * @see MPU6050_RA_MOT_DETECT_CTRL
	 * @see MPU6050_DETECT_ACCEL_ON_DELAY_BIT
	 */
	void setAccelerometerPowerOnDelay(int delay) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_MOT_DETECT_CTRL, MPU6050_DETECT_ACCEL_ON_DELAY_BIT, MPU6050_DETECT_ACCEL_ON_DELAY_LENGTH, delay);
	}
	/** Get Free Fall detection counter decrement configuration.
	 * Detection is registered by the Free Fall detection module after accelerometer
	 * measurements meet their respective threshold conditions over a specified
	 * number of samples. When the threshold conditions are met, the corresponding
	 * detection counter increments by 1. The user may control the rate at which the
	 * detection counter decrements when the threshold condition is not met by
	 * configuring FF_COUNT. The decrement rate can be set according to the
	 * following table:
	 *
	 * <pre>
	 * FF_COUNT | Counter Decrement
	 * ---------+------------------
	 * 0        | Reset
	 * 1        | 1
	 * 2        | 2
	 * 3        | 4
	 * </pre>
	 *
	 * When FF_COUNT is configured to 0 (reset), any non-qualifying sample will
	 * reset the counter to 0. For further information on Free Fall detection,
	 * please refer to Registers 29 to 32.
	 *
	 * @return Current decrement configuration
	 * @see MPU6050_RA_MOT_DETECT_CTRL
	 * @see MPU6050_DETECT_FF_COUNT_BIT
	 */
	int getFreefallDetectionCounterDecrement() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_MOT_DETECT_CTRL, MPU6050_DETECT_FF_COUNT_BIT, MPU6050_DETECT_FF_COUNT_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set Free Fall detection counter decrement configuration.
	 * @param decrement New decrement configuration value
	 * @see getFreefallDetectionCounterDecrement()
	 * @see MPU6050_RA_MOT_DETECT_CTRL
	 * @see MPU6050_DETECT_FF_COUNT_BIT
	 */
	void setFreefallDetectionCounterDecrement(int decrement) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_MOT_DETECT_CTRL, MPU6050_DETECT_FF_COUNT_BIT, MPU6050_DETECT_FF_COUNT_LENGTH, decrement);
	}
	/** Get Motion detection counter decrement configuration.
	 * Detection is registered by the Motion detection module after accelerometer
	 * measurements meet their respective threshold conditions over a specified
	 * number of samples. When the threshold conditions are met, the corresponding
	 * detection counter increments by 1. The user may control the rate at which the
	 * detection counter decrements when the threshold condition is not met by
	 * configuring MOT_COUNT. The decrement rate can be set according to the
	 * following table:
	 *
	 * <pre>
	 * MOT_COUNT | Counter Decrement
	 * ----------+------------------
	 * 0         | Reset
	 * 1         | 1
	 * 2         | 2
	 * 3         | 4
	 * </pre>
	 *
	 * When MOT_COUNT is configured to 0 (reset), any non-qualifying sample will
	 * reset the counter to 0. For further information on Motion detection,
	 * please refer to Registers 29 to 32.
	 *
	 */
	int getMotionDetectionCounterDecrement() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_MOT_DETECT_CTRL, MPU6050_DETECT_MOT_COUNT_BIT, MPU6050_DETECT_MOT_COUNT_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set Motion detection counter decrement configuration.
	 * @param decrement New decrement configuration value
	 * @see getMotionDetectionCounterDecrement()
	 * @see MPU6050_RA_MOT_DETECT_CTRL
	 * @see MPU6050_DETECT_MOT_COUNT_BIT
	 */
	void setMotionDetectionCounterDecrement(int decrement) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_MOT_DETECT_CTRL, MPU6050_DETECT_MOT_COUNT_BIT, MPU6050_DETECT_MOT_COUNT_LENGTH, decrement);
	}

	// USER_CTRL register

	/** Get FIFO enabled status.
	 * When this bit is set to 0, the FIFO buffer is disabled. The FIFO buffer
	 * cannot be written to or read from while disabled. The FIFO buffer's state
	 * does not change unless the MPU-60X0 is power cycled.
	 * @return Current FIFO enabled status
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_FIFO_EN_BIT
	 */
	boolean getFIFOEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_FIFO_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set FIFO enabled status.
	 * @param enabled New FIFO enabled status
	 * @see getFIFOEnabled()
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_FIFO_EN_BIT
	 */
	void setFIFOEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_FIFO_EN_BIT, enabled);
	}
	/** Get I2C Master Mode enabled status.
	 * When this mode is enabled, the MPU-60X0 acts as the I2C Master to the
	 * external sensor slave devices on the auxiliary I2C bus. When this bit is
	 * cleared to 0, the auxiliary I2C bus lines (AUX_DA and AUX_CL) are logically
	 * driven by the primary I2C bus (SDA and SCL). This is a precondition to
	 * enabling Bypass Mode. For further information regarding Bypass Mode, please
	 * refer to Register 55.
	 * @return Current I2C Master Mode enabled status
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_I2C_MST_EN_BIT
	 */
	boolean getI2CMasterModeEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_I2C_MST_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set I2C Master Mode enabled status.
	 * @param enabled New I2C Master Mode enabled status
	 * @see getI2CMasterModeEnabled()
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_I2C_MST_EN_BIT
	 */
	void setI2CMasterModeEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_I2C_MST_EN_BIT, enabled);
	}
	/** Switch from I2C to SPI mode (MPU-6000 only)
	 * If this is set, the primary SPI interface will be enabled in place of the
	 * disabled primary I2C interface.
	 */
	void switchSPIEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_I2C_IF_DIS_BIT, enabled);
	}
	/** Reset the FIFO.
	 * This bit resets the FIFO buffer when set to 1 while FIFO_EN equals 0. This
	 * bit automatically clears to 0 after the reset has been triggered.
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_FIFO_RESET_BIT
	 */
	void resetFIFO() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_FIFO_RESET_BIT, true);
	}
	/** Reset the I2C Master.
	 * This bit resets the I2C Master when set to 1 while I2C_MST_EN equals 0.
	 * This bit automatically clears to 0 after the reset has been triggered.
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_I2C_MST_RESET_BIT
	 */
	void resetI2CMaster() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_I2C_MST_RESET_BIT, true);
	}
	/** Reset all sensor registers and signal paths.
	 * When set to 1, this bit resets the signal paths for all sensors (gyroscopes,
	 * accelerometers, and temperature sensor). This operation will also clear the
	 * sensor registers. This bit automatically clears to 0 after the reset has been
	 * triggered.
	 *
	 * When resetting only the signal path (and not the sensor registers), please
	 * use Register 104, SIGNAL_PATH_RESET.
	 *
	 * @see MPU6050_RA_USER_CTRL
	 * @see MPU6050_USERCTRL_SIG_COND_RESET_BIT
	 */
	void resetSensors() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_SIG_COND_RESET_BIT, true);
	}

	// PWR_MGMT_1 register

	/** Trigger a full device reset.
	 * A small delay of ~50ms may be desirable after triggering a reset.
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_DEVICE_RESET_BIT
	 */
	void reset() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_DEVICE_RESET_BIT, true);
	}
	/** Get sleep mode status.
	 * Setting the SLEEP bit in the register puts the device into very low power
	 * sleep mode. In this mode, only the serial interface and internal registers
	 * remain active, allowing for a very low standby current. Clearing this bit
	 * puts the device back into normal mode. To save power, the individual standby
	 * selections for each of the gyros should be used if any gyro axis is not used
	 * by the application.
	 * @return Current sleep mode enabled status
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_SLEEP_BIT
	 */
	boolean getSleepEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_SLEEP_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set sleep mode status.
	 * @param enabled New sleep mode enabled status
	 * @see getSleepEnabled()
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_SLEEP_BIT
	 */
	void setSleepEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_SLEEP_BIT, enabled);
	}
	/** Get wake cycle enabled status.
	 * When this bit is set to 1 and SLEEP is disabled, the MPU-60X0 will cycle
	 * between sleep mode and waking up to take a single sample of data from active
	 * sensors at a rate determined by LP_WAKE_CTRL (register 108).
	 * @return Current sleep mode enabled status
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_CYCLE_BIT
	 */
	boolean getWakeCycleEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_CYCLE_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set wake cycle enabled status.
	 * @param enabled New sleep mode enabled status
	 * @see getWakeCycleEnabled()
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_CYCLE_BIT
	 */
	void setWakeCycleEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_CYCLE_BIT, enabled);
	}
	/** Get temperature sensor enabled status.
	 * Control the usage of the internal temperature sensor.
	 *
	 * Note: this register stores the *disabled* value, but for consistency with the
	 * rest of the code, the function is named and used with standard true/false
	 * values to indicate whether the sensor is enabled or disabled, respectively.
	 *
	 * @return Current temperature sensor enabled status
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_TEMP_DIS_BIT
	 */
	boolean getTempSensorEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_TEMP_DIS_BIT, bytebuffer);
	    return bytebuffer != 0; // 1 is actually disabled here
	}
	/** Set temperature sensor enabled status.
	 * Note: this register stores the *disabled* value, but for consistency with the
	 * rest of the code, the function is named and used with standard true/false
	 * values to indicate whether the sensor is enabled or disabled, respectively.
	 *
	 * @param enabled New temperature sensor enabled status
	 * @see getTempSensorEnabled()
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_TEMP_DIS_BIT
	 */
	void setTempSensorEnabled(boolean enabled) {
	    // 1 is actually disabled here
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_TEMP_DIS_BIT, !enabled);
	}
	/** Get clock source setting.
	 * @return Current clock source setting
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_CLKSEL_BIT
	 * @see MPU6050_PWR1_CLKSEL_LENGTH
	 */
	int getClockSource() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_CLKSEL_BIT, MPU6050_PWR1_CLKSEL_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set clock source setting.
	 * An internal 8MHz oscillator, gyroscope based clock, or external sources can
	 * be selected as the MPU-60X0 clock source. When the internal 8 MHz oscillator
	 * or an external source is chosen as the clock source, the MPU-60X0 can operate
	 * in low power modes with the gyroscopes disabled.
	 *
	 * Upon power up, the MPU-60X0 clock source defaults to the internal oscillator.
	 * However, it is highly recommended that the device be configured to use one of
	 * the gyroscopes (or an external clock source) as the clock reference for
	 * improved stability. The clock source can be selected according to the following table:
	 *
	 * <pre>
	 * CLK_SEL | Clock Source
	 * --------+--------------------------------------
	 * 0       | Internal oscillator
	 * 1       | PLL with X Gyro reference
	 * 2       | PLL with Y Gyro reference
	 * 3       | PLL with Z Gyro reference
	 * 4       | PLL with external 32.768kHz reference
	 * 5       | PLL with external 19.2MHz reference
	 * 6       | Reserved
	 * 7       | Stops the clock and keeps the timing generator in reset
	 * </pre>
	 *
	 * @param source New clock source setting
	 * @see getClockSource()
	 * @see MPU6050_RA_PWR_MGMT_1
	 * @see MPU6050_PWR1_CLKSEL_BIT
	 * @see MPU6050_PWR1_CLKSEL_LENGTH
	 */
	void setClockSource(int source) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_PWR_MGMT_1, MPU6050_PWR1_CLKSEL_BIT, MPU6050_PWR1_CLKSEL_LENGTH, source);
	}

	// PWR_MGMT_2 register

	/** Get wake frequency in Accel-Only Low Power Mode.
	 * The MPU-60X0 can be put into Accerlerometer Only Low Power Mode by setting
	 * PWRSEL to 1 in the Power Management 1 register (Register 107). In this mode,
	 * the device will power off all devices except for the primary I2C interface,
	 * waking only the accelerometer at fixed intervals to take a single
	 * measurement. The frequency of wake-ups can be configured with LP_WAKE_CTRL
	 * as shown below:
	 *
	 * <pre>
	 * LP_WAKE_CTRL | Wake-up Frequency
	 * -------------+------------------
	 * 0            | 1.25 Hz
	 * 1            | 2.5 Hz
	 * 2            | 5 Hz
	 * 3            | 10 Hz
	 * </pre>
	 *
	 * For further information regarding the MPU-60X0's power modes, please refer to
	 * Register 107.
	 *
	 * @return Current wake frequency
	 * @see MPU6050_RA_PWR_MGMT_2
	 */
	int getWakeFrequency() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_LP_WAKE_CTRL_BIT, MPU6050_PWR2_LP_WAKE_CTRL_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set wake frequency in Accel-Only Low Power Mode.
	 * @param frequency New wake frequency
	 * @see MPU6050_RA_PWR_MGMT_2
	 */
	void setWakeFrequency(int frequency) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_LP_WAKE_CTRL_BIT, MPU6050_PWR2_LP_WAKE_CTRL_LENGTH, frequency);
	}

	/** Get X-axis accelerometer standby enabled status.
	 * If enabled, the X-axis will not gather or report data (or use power).
	 * @return Current X-axis standby enabled status
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_XA_BIT
	 */
	boolean getStandbyXAccelEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_XA_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set X-axis accelerometer standby enabled status.
	 * @param New X-axis standby enabled status
	 * @see getStandbyXAccelEnabled()
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_XA_BIT
	 */
	void setStandbyXAccelEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_XA_BIT, enabled);
	}
	/** Get Y-axis accelerometer standby enabled status.
	 * If enabled, the Y-axis will not gather or report data (or use power).
	 * @return Current Y-axis standby enabled status
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_YA_BIT
	 */
	boolean getStandbyYAccelEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_YA_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Y-axis accelerometer standby enabled status.
	 * @param New Y-axis standby enabled status
	 * @see getStandbyYAccelEnabled()
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_YA_BIT
	 */
	void setStandbyYAccelEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_YA_BIT, enabled);
	}
	/** Get Z-axis accelerometer standby enabled status.
	 * If enabled, the Z-axis will not gather or report data (or use power).
	 * @return Current Z-axis standby enabled status
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_ZA_BIT
	 */
	boolean getStandbyZAccelEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_ZA_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Z-axis accelerometer standby enabled status.
	 * @param New Z-axis standby enabled status
	 * @see getStandbyZAccelEnabled()
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_ZA_BIT
	 */
	void setStandbyZAccelEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_ZA_BIT, enabled);
	}
	/** Get X-axis gyroscope standby enabled status.
	 * If enabled, the X-axis will not gather or report data (or use power).
	 * @return Current X-axis standby enabled status
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_XG_BIT
	 */
	boolean getStandbyXGyroEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_XG_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set X-axis gyroscope standby enabled status.
	 * @param New X-axis standby enabled status
	 * @see getStandbyXGyroEnabled()
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_XG_BIT
	 */
	void setStandbyXGyroEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_XG_BIT, enabled);
	}
	/** Get Y-axis gyroscope standby enabled status.
	 * If enabled, the Y-axis will not gather or report data (or use power).
	 * @return Current Y-axis standby enabled status
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_YG_BIT
	 */
	boolean getStandbyYGyroEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_YG_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Y-axis gyroscope standby enabled status.
	 * @param New Y-axis standby enabled status
	 * @see getStandbyYGyroEnabled()
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_YG_BIT
	 */
	void setStandbyYGyroEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_YG_BIT, enabled);
	}
	/** Get Z-axis gyroscope standby enabled status.
	 * If enabled, the Z-axis will not gather or report data (or use power).
	 * @return Current Z-axis standby enabled status
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_ZG_BIT
	 */
	boolean getStandbyZGyroEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_ZG_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	/** Set Z-axis gyroscope standby enabled status.
	 * @param New Z-axis standby enabled status
	 * @see getStandbyZGyroEnabled()
	 * @see MPU6050_RA_PWR_MGMT_2
	 * @see MPU6050_PWR2_STBY_ZG_BIT
	 */
	void setStandbyZGyroEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_PWR_MGMT_2, MPU6050_PWR2_STBY_ZG_BIT, enabled);
	}

	// FIFO_COUNT* registers

	/** Get current FIFO buffer size.
	 * This value indicates the number of bytes stored in the FIFO buffer. This
	 * number is in turn the number of bytes that can be read from the FIFO buffer
	 * and it is directly proportional to the number of samples available given the
	 * set of sensor data bound to be stored in the FIFO (register 35 and 36).
	 * @return Current FIFO buffer size
	 */
	int getFIFOCount() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_FIFO_COUNTH, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}

	// FIFO_R_W register

	/** Get byte from FIFO buffer.
	 * This register is used to read and write data from the FIFO buffer. Data is
	 * written to the FIFO in order of register number (from lowest to highest). If
	 * all the FIFO enable flags (see below) are enabled and all External Sensor
	 * Data registers (Registers 73 to 96) are associated with a Slave device, the
	 * contents of registers 59 through 96 will be written in order at the Sample
	 * Rate.
	 *
	 * The contents of the sensor data registers (Registers 59 to 96) are written
	 * into the FIFO buffer when their corresponding FIFO enable flags are set to 1
	 * in FIFO_EN (Register 35). An additional flag for the sensor data registers
	 * associated with I2C Slave 3 can be found in I2C_MST_CTRL (Register 36).
	 *
	 * If the FIFO buffer has overflowed, the status bit FIFO_OFLOW_INT is
	 * automatically set to 1. This bit is located in INT_STATUS (Register 58).
	 * When the FIFO buffer has overflowed, the oldest data will be lost and new
	 * data will be written to the FIFO.
	 *
	 * If the FIFO buffer is empty, reading this register will return the last byte
	 * that was previously read from the FIFO until new data is available. The user
	 * should check FIFO_COUNT to ensure that the FIFO buffer is not read when
	 * empty.
	 *
	 * @return Byte from FIFO buffer
	 */
	int getFIFOByte() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_FIFO_R_W, bytebuffer);
	    return bytebuffer;
	}
	void getFIFOBytes(int[] data, int length) {
	    if(length > 0){
	        I2CdevReadBytes(deviceAddress, MPU6050_RA_FIFO_R_W, length, data);
	    } else {
	    	data = new int[0];
	    }
	}
	/** Write byte to FIFO buffer.
	 * @see getFIFOByte()
	 * @see MPU6050_RA_FIFO_R_W
	 */
	void setFIFOByte(int data) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_FIFO_R_W, data);
	}

	// WHO_AM_I register

	/** Get Device ID.
	 * This register is used to verify the identity of the device (0b110100, 0x34).
	 * @return Device ID (6 bits only! should be 0x34)
	 * @see MPU6050_RA_WHO_AM_I
	 * @see MPU6050_WHO_AM_I_BIT
	 * @see MPU6050_WHO_AM_I_LENGTH
	 */
	int getDeviceID() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_WHO_AM_I, MPU6050_WHO_AM_I_BIT, MPU6050_WHO_AM_I_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	/** Set Device ID.
	 * Write a new ID into the WHO_AM_I register (no idea why this should ever be
	 * necessary though).
	 * @param id New device ID to set.
	 * @see getDeviceID()
	 * @see MPU6050_RA_WHO_AM_I
	 * @see MPU6050_WHO_AM_I_BIT
	 * @see MPU6050_WHO_AM_I_LENGTH
	 */
	void setDeviceID(int id) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_WHO_AM_I, MPU6050_WHO_AM_I_BIT, MPU6050_WHO_AM_I_LENGTH, id);
	}

	// ======== UNDOCUMENTED/DMP REGISTERS/METHODS ========

	// XG_OFFS_TC register

	int getOTPBankValid() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_XG_OFFS_TC, MPU6050_TC_OTP_BNK_VLD_BIT, bytebuffer);
	    return bytebuffer;
	}
	
	void setOTPBankValid(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_XG_OFFS_TC, MPU6050_TC_OTP_BNK_VLD_BIT, enabled);
	}
	
	int getXGyroOffsetTC() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_XG_OFFS_TC, MPU6050_TC_OFFSET_BIT, MPU6050_TC_OFFSET_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	
	void setXGyroOffsetTC(int offset) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_XG_OFFS_TC, MPU6050_TC_OFFSET_BIT, MPU6050_TC_OFFSET_LENGTH, offset);
	}

	// YG_OFFS_TC register

	int getYGyroOffsetTC() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_YG_OFFS_TC, MPU6050_TC_OFFSET_BIT, MPU6050_TC_OFFSET_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	void setYGyroOffsetTC(int offset) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_YG_OFFS_TC, MPU6050_TC_OFFSET_BIT, MPU6050_TC_OFFSET_LENGTH, offset);
	}

	// ZG_OFFS_TC register

	int getZGyroOffsetTC() {
	    I2CdevReadBits(deviceAddress, MPU6050_RA_ZG_OFFS_TC, MPU6050_TC_OFFSET_BIT, MPU6050_TC_OFFSET_LENGTH, bytebuffer);
	    return bytebuffer;
	}
	void setZGyroOffsetTC(int offset) {
	    I2CdevWriteBits(deviceAddress, MPU6050_RA_ZG_OFFS_TC, MPU6050_TC_OFFSET_BIT, MPU6050_TC_OFFSET_LENGTH, offset);
	}

	// X_FINE_GAIN register

	int getXFineGain() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_X_FINE_GAIN, bytebuffer);
	    return bytebuffer;
	}
	void setXFineGain(int gain) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_X_FINE_GAIN, gain);
	}

	// Y_FINE_GAIN register

	int getYFineGain() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_Y_FINE_GAIN, bytebuffer);
	    return bytebuffer;
	}
	void setYFineGain(int gain) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_Y_FINE_GAIN, gain);
	}

	// Z_FINE_GAIN register

	int getZFineGain() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_Z_FINE_GAIN, bytebuffer);
	    return bytebuffer;
	}
	void setZFineGain(int gain) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_Z_FINE_GAIN, gain);
	}

	// XA_OFFS_* registers

	int getXAccelOffset() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_XA_OFFS_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	void setXAccelOffset(int offset) {
	    I2CdevWriteWord(deviceAddress, MPU6050_RA_XA_OFFS_H, offset);
	}

	// YA_OFFS_* register

	int getYAccelOffset() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_YA_OFFS_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	void setYAccelOffset(int offset) {
	    I2CdevWriteWord(deviceAddress, MPU6050_RA_YA_OFFS_H, offset);
	}

	// ZA_OFFS_* register

	int getZAccelOffset() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ZA_OFFS_H, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	void setZAccelOffset(int offset) {
	    I2CdevWriteWord(deviceAddress, MPU6050_RA_ZA_OFFS_H, offset);
	}

	// XG_OFFS_USR* registers

	int getXGyroOffset() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_XG_OFFS_USRH, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	void setXGyroOffset(int offset) {
	    I2CdevWriteWord(deviceAddress, MPU6050_RA_XG_OFFS_USRH, offset);
	}

	// YG_OFFS_USR* register

	int getYGyroOffset() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_YG_OFFS_USRH, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	void setYGyroOffset(int offset) {
	    I2CdevWriteWord(deviceAddress, MPU6050_RA_YG_OFFS_USRH, offset);
	}

	// ZG_OFFS_USR* register

	int getZGyroOffset() {
	    I2CdevReadBytes(deviceAddress, MPU6050_RA_ZG_OFFS_USRH, 2, buffer);
	    return (((int)buffer[0]) << 8) | buffer[1];
	}
	void setZGyroOffset(int offset) {
	    I2CdevWriteWord(deviceAddress, MPU6050_RA_ZG_OFFS_USRH, offset);
	}

	// INT_ENABLE register (DMP functions)

	boolean getIntPLLReadyEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_PLL_RDY_INT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	void setIntPLLReadyEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_PLL_RDY_INT_BIT, enabled);
	}
	boolean getIntDMPEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_DMP_INT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	void setIntDMPEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_INT_ENABLE, MPU6050_INTERRUPT_DMP_INT_BIT, enabled);
	}

	// DMP_INT_STATUS

	boolean getDMPInt5Status() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_DMP_INT_STATUS, MPU6050_DMPINT_5_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	boolean getDMPInt4Status() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_DMP_INT_STATUS, MPU6050_DMPINT_4_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	boolean getDMPInt3Status() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_DMP_INT_STATUS, MPU6050_DMPINT_3_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	boolean getDMPInt2Status() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_DMP_INT_STATUS, MPU6050_DMPINT_2_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	boolean getDMPInt1Status() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_DMP_INT_STATUS, MPU6050_DMPINT_1_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	boolean getDMPInt0Status() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_DMP_INT_STATUS, MPU6050_DMPINT_0_BIT, bytebuffer);
	    return bytebuffer != 0;
	}

	// INT_STATUS register (DMP functions)

	boolean getIntPLLReadyStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_PLL_RDY_INT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	boolean getIntDMPStatus() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_INT_STATUS, MPU6050_INTERRUPT_DMP_INT_BIT, bytebuffer);
	    return bytebuffer != 0;
	}

	// USER_CTRL register (DMP functions)

	boolean getDMPEnabled() {
	    I2CdevReadBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_DMP_EN_BIT, bytebuffer);
	    return bytebuffer != 0;
	}
	void setDMPEnabled(boolean enabled) {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_DMP_EN_BIT, enabled);
	}
	void resetDMP() {
	    I2CdevWriteBit(deviceAddress, MPU6050_RA_USER_CTRL, MPU6050_USERCTRL_DMP_RESET_BIT, true);
	}

	// BANK_SEL register

	void setMemoryBank(int bank, boolean prefetchEnabled, boolean userBank) {
	    bank = bank & 0x1F;
	    if (userBank) bank |= 0x20;
	    if (prefetchEnabled) bank |= 0x40;
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_BANK_SEL, bank);
	}

	void setMemoryBank(int bank) {
		setMemoryBank(bank, false, false);
	}
	
	// MEM_START_ADDR register

	void setMemoryStartAddress(int address) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_MEM_START_ADDR, address);
	}

	// MEM_R_W register

	int readMemoryByte() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_MEM_R_W, bytebuffer);
	    return bytebuffer;
	}
	void writeMemoryByte(int data) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_MEM_R_W, data);
	}
	void readMemoryBlock(int[] data, int dataSize, int bank, int address) {
	    setMemoryBank(bank);
	    setMemoryStartAddress(address);
	    int chunkSize;
	    for (int i = 0; i < dataSize;) {
	        // determine correct chunk size according to bank position and data size
	        chunkSize = MPU6050_DMP_MEMORY_CHUNK_SIZE;

	        // make sure we don't go past the data size
	        if (i + chunkSize > dataSize) chunkSize = dataSize - i;

	        // make sure this chunk doesn't go past the bank boundary (256 bytes)
	        if (chunkSize > 256 - address) chunkSize = 256 - address;

	        // read the chunk of data as specified
	        I2CdevReadBytes(deviceAddress, MPU6050_RA_MEM_R_W, chunkSize, data);
	        
	        // increase byte index by [chunkSize]
	        i += chunkSize;

	        // int automatically wraps to 0 at 256
	        address += chunkSize;

	        // if we aren't done, update bank (if necessary) and address
	        if (i < dataSize) {
	            if (address == 0) bank++;
	            setMemoryBank(bank);
	            setMemoryStartAddress(address);
	        }
	    }
	}
	boolean writeMemoryBlock(int[] data, int dataSize, int bank, int address, boolean verify) {
	    setMemoryBank(bank);
	    setMemoryStartAddress(address);
	    int chunkSize;
	    int[] verifyBuffer;
	    int[] progBuffer;
	    int i;
	    int j;
	    if (verify){ 
	    	verifyBuffer = new int[MPU6050_DMP_MEMORY_CHUNK_SIZE];
	    }
	    else {
	    	verifyBuffer = new int[0];
	    }

	    for (i = 0; i < dataSize;) {
	        // determine correct chunk size according to bank position and data size
	        chunkSize = MPU6050_DMP_MEMORY_CHUNK_SIZE;

	        // make sure we don't go past the data size
	        if (i + chunkSize > dataSize) chunkSize = dataSize - i;

	        // make sure this chunk doesn't go past the bank boundary (256 bytes)
	        if (chunkSize > 256 - address) chunkSize = 256 - address;
	        
	        // write the chunk of data as specified
	        // progBuffer = (int *)data + i;
	        progBuffer = new int[chunkSize];
	        for (j=0; j< chunkSize; j++){
	        	progBuffer[j] = data[i+j];
	        }

	        I2CdevWriteBytes(deviceAddress, MPU6050_RA_MEM_R_W, chunkSize, progBuffer);

	        // verify data if needed
	        
	        if (verify && (verifyBuffer.length > 0)) {
	            setMemoryBank(bank);
	            setMemoryStartAddress(address);
	            I2CdevReadBytes(deviceAddress, MPU6050_RA_MEM_R_W, chunkSize, verifyBuffer);
	            if (memcmp(progBuffer, verifyBuffer, chunkSize) != 0) {
	                /*Serial.print("Block write verification error, bank ");
	                Serial.print(bank, DEC);
	                Serial.print(", address ");
	                Serial.print(address, DEC);
	                Serial.print("!\nExpected:");
	                for (j = 0; j < chunkSize; j++) {
	                    Serial.print(" 0x");
	                    if (progBuffer[j] < 16) Serial.print("0");
	                    Serial.print(progBuffer[j], HEX);
	                }
	                Serial.print("\nReceived:");
	                for (int j = 0; j < chunkSize; j++) {
	                    Serial.print(" 0x");
	                    if (verifyBuffer[i + j] < 16) Serial.print("0");
	                    Serial.print(verifyBuffer[i + j], HEX);
	                }
	                Serial.print("\n");*/
	                //* free(verifyBuffer);
	                return false; // uh oh.
	            }
	        }
			
	        // increase byte index by [chunkSize]
	        i += chunkSize;

	        // int automatically wraps to 0 at 256
	        address += chunkSize;

	        // if we aren't done, update bank (if necessary) and address
	        if (i < dataSize) {
	            if (address == 0) bank++;
	            setMemoryBank(bank);
	            setMemoryStartAddress(address);
	        }
	    }
	    //* if (verify) free(verifyBuffer);
	    return true;
	}
	boolean writeProgMemoryBlock(int[] data, int dataSize, int bank, int address, boolean verify) {
	    return writeMemoryBlock(data, dataSize, bank, address, verify);
	}
	boolean writeProgMemoryBlock(int[] data, int dataSize) {
	    return writeMemoryBlock(data, dataSize, 0, 0, true);
	}
	
	boolean writeDMPConfigurationSet(int[] data, int dataSize) {
	    int[] progBuffer;
		int special;
		boolean success = false;
	    int i;

	    // config set data is a long string of blocks with the following structure:
	    // [bank] [offset] [length] [byte[0], byte[1], ..., byte[length]]
	    int bank, offset, length;
	    for (i = 0; i < dataSize;) {
	        bank = data[i++];
	        offset = data[i++];
	        length = data[i++];

	        // write data or perform special action
	        if (length > 0) {
	            // regular block of data to write
	            /*Serial.print("Writing config block to bank ");
	            Serial.print(bank);
	            Serial.print(", offset ");
	            Serial.print(offset);
	            Serial.print(", length=");
	            Serial.println(length);*/
	            //progBuffer = (int *)data + i;
	        	progBuffer = new int[length];
	        	for (int k=0; k<length;k++){
	        		progBuffer[k] = data[i+k]; 
	        	}
	        	success = writeMemoryBlock(progBuffer, length, bank, offset, true);
	            i += length;
	        } else {
	            // special instruction
	            // NOTE: this kind of behavior (what and when to do certain things)
	            // is totally undocumented. This code is in here based on observed
	            // behavior only, and exactly why (or even whether) it has to be here
	            // is anybody's guess for now.
	                special = data[i++];
	            /*Serial.print("Special command code ");
	            Serial.print(special, HEX);
	            Serial.println(" found...");*/
	            if (special == 0x01) {
	                // enable DMP-related interrupts
	                
	                //setIntZeroMotionEnabled(true);
	                //setIntFIFOBufferOverflowEnabled(true);
	                //setIntDMPEnabled(true);
	                I2CdevWriteByte(deviceAddress, MPU6050_RA_INT_ENABLE, 0x32);  // single operation

	                success = true;
	            } else {
	                // unknown special command
	                success = false;
	            }
	        }
	        
	        if (!success) {
	            return false; // uh oh
	        }
	    }
	    return true;
	}
	    
	boolean writeProgDMPConfigurationSet(int[] data, int dataSize) {
	    return writeDMPConfigurationSet(data, dataSize);
	}
	// DMP_CFG_1 register

	int getDMPConfig1() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_DMP_CFG_1, bytebuffer);
	    return bytebuffer;
	}
	void setDMPConfig1(int config) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_DMP_CFG_1, config);
	}

	// DMP_CFG_2 register

	int getDMPConfig2() {
	    I2CdevReadByte(deviceAddress, MPU6050_RA_DMP_CFG_2, bytebuffer);
	    return bytebuffer;
	}
	void setDMPConfig2(int config) {
	    I2CdevWriteByte(deviceAddress, MPU6050_RA_DMP_CFG_2, config);
	}
	//* Compare the content of two buffers
	//  Returns 0 if the two buffers have the same content otherwise 1
	int memcmp(int[] buffer1, int[] buffer2, int length ){
		int result = 0;
		for (int i=0 ; i < length ; i++){
			if (buffer1[i] != buffer2[i]){
				result = 1;
			}
		}
		return result;
	}
	
	int pgm_read_byte(int a){
		return 0;
	}
	/* Start of  I2CDEV section. Most of this code could be moved to and implemented in
	 * the I2CControl interface. 
	 */
	
	/** Read a single bit from an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to read from
	 * @param bitNum Bit position to read (0-7)
	 * @param data Container for single bit value
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Status of read operation (true = success)
	 */
	int I2CdevReadBit(int devAddr, int regAddr, int bitNum, int data, int timeout) {
	    int bitmask = 1;
	    int bytebuffer = data;
	    int count = I2CdevReadByte(devAddr, regAddr, bytebuffer, timeout);
	    data = bytebuffer & (bitmask << bitNum);
	    return count;
	}
	int I2CdevReadBit(int devAddr, int regAddr, int bitNum, int data) {
	    return I2CdevReadBit(devAddr, regAddr, bitNum, data, timeout);
	}
	/** Read a single bit from a 16-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to read from
	 * @param bitNum Bit position to read (0-15)
	 * @param data Container for single bit value
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Status of read operation (true = success)
	 */
	
	int I2CdevReadBitW(int devAddr, int regAddr, int bitNum, int data, int timeout) {
	    int b = 0;
	    int bitmask = 1;
	    int count = I2CdevReadWord(devAddr, regAddr, b, timeout);
	    data = b & (bitmask << bitNum);
	    return count;
	}

	/** Read multiple bits from an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to read from
	 * @param bitStart First bit position to read (0-7)
	 * @param length Number of bits to read (not more than 8)
	 * @param data Container for right-aligned value (i.e. '101' read from any bitStart position will equal 0x05)
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Status of read operation (true = success)
	 */
	int I2CdevReadBits(int devAddr, int regAddr, int bitStart, int length, int data, int timeout) {
    // 01101001 read byte
    // 76543210 bit numbers
    //    xxx   args: bitStart=4, length=3
    //    010   masked
    //   -> 010 shifted
		int count, b = 0;
		if ((count = I2CdevReadByte(devAddr, regAddr, b, timeout)) != 0) {
			int mask = ((1 << length) - 1) << (bitStart - length + 1);
			b &= mask;
			b >>= (bitStart - length + 1);
			data = b;
		}
		return count;
	}
	int I2CdevReadBits(int devAddr, int regAddr, int bitStart, int length, int data) {
			return I2CdevReadBits(devAddr, regAddr, bitStart, length, data, timeout);
		}
	/** Read multiple bits from a 16-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to read from
	 * @param bitStart First bit position to read (0-15)
	 * @param length Number of bits to read (not more than 16)
	 * @param data Container for right-aligned value (i.e. '101' read from any bitStart position will equal 0x05)
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Status of read operation (1 = success, 0 = failure, -1 = timeout)
	 */
	int I2CdevReadBitsW(int devAddr, int regAddr, int bitStart, int length, int data, int timeout) {
	    // 1101011001101001 read byte
	    // fedcba9876543210 bit numbers
	    //    xxx           args: bitStart=12, length=3
	    //    010           masked
	    //           -> 010 shifted
	    int count;
	    int w = 0;
	    if ((count = I2CdevReadWord(devAddr, regAddr, w, timeout)) != 0) {
	        int mask = ((1 << length) - 1) << (bitStart - length + 1);
	        w &= mask;
	        w >>= (bitStart - length + 1);
	        data = w;
	    }
	    return count;
	}

	/** Read single byte from an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to read from
	 * @param data Container for byte value read from device
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Status of read operation (true = success)
	 */
	int I2CdevReadByte(int devAddr, int regAddr, int data, int timeout) {
		int bytebuffer[] = {data};
		int status = I2CdevReadBytes(devAddr, regAddr, 1, bytebuffer, timeout);
		data = bytebuffer[0];
		return status;
	}

	/** Read single word from a 16-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to read from
	 * @param data Container for word value read from device
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Status of read operation (true = success)
	 */
	int I2CdevReadWord(int devAddr, int regAddr, int data, int timeout) {
		int[] readbuffer = {data};
		int status = I2CdevReadWords(devAddr, regAddr, 1, readbuffer, timeout);
		data = readbuffer[0];
		return status;
	}
	
	int I2CdevReadWord(int devAddr, int regAddr, int data) {
		return I2CdevReadWord(devAddr, regAddr, data, timeout);
	}
	/** Read multiple words from a 16-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr First register regAddr to read from
	 * @param length Number of words to read
	 * @param data Buffer to store read data in
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Number of words read (-1 indicates failure)
	 */
	int I2CdevReadWords(int devAddr, int regAddr, int length, int[] data, int timeout) {
		byte bytebuffer[] = new byte[length *2]; 
		controller.i2cRead(busAddress, devAddr, bytebuffer, bytebuffer.length);
		for (int i=0; i < bytebuffer.length ; i++){
			data[i] = bytebuffer[i*2] <<8 + bytebuffer[i*2+1] & 0xff;
		}
		return length;
	}
	
	int I2CdevReadByte(int devAddr, int regAddr, int data) {
		int bytebuffer [] = {data};
	    return I2CdevReadBytes(devAddr, regAddr, 1, bytebuffer, timeout);
	}
	/** Read multiple bytes from an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr First register regAddr to read from
	 * @param length Number of bytes to read
	 * @param data Buffer to store read data in
	 * @param timeout Optional read timeout in milliseconds (0 to disable, leave off to use default class value in I2Cdev::readTimeout)
	 * @return Number of bytes read (-1 indicates failure)
	 */
	// TODO Return the correct length
	int I2CdevReadBytes(int devAddr, int regAddr, int length, int[] data, int timeout) {
		byte[] writebuffer = new byte[] {(byte)regAddr}; 
		byte[] readbuffer = new byte[length]; 
		controller.i2cWrite(busAddress, deviceAddress, writebuffer, writebuffer.length);
		controller.i2cRead(busAddress, deviceAddress, readbuffer, length);
		for (int i=0; i < length ; i++){
			data[i] = readbuffer[i] & 0xff;
		}
		return length;
	}
	int I2CdevReadBytes(int devAddr, int regAddr, int length, int[] data) {
		return I2CdevReadBytes(devAddr, regAddr, length, data, timeout);
	}
	/** Write multiple bits in an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to write to
	 * @param bitStart First bit position to write (0-7)
	 * @param length Number of bits to write (not more than 8)
	 * @param data Right-aligned value to write
	 * @return Status of operation (true = success)
	 */
	boolean I2CdevWriteBits(int devAddr, int regAddr, int bitStart, int length, int data) {
	    //      010 value to write
	    // 76543210 bit numbers
	    //    xxx   args: bitStart=4, length=3
	    // 00011100 mask byte
	    // 10101111 original value (sample)
	    // 10100011 original & ~mask
	    // 10101011 masked | value
	    int b = 0;
	    if (I2CdevReadByte(devAddr, regAddr, b) != 0) {
	        int mask = ((1 << length) - 1) << (bitStart - length + 1);
	        data <<= (bitStart - length + 1); // shift data into correct position
	        data &= mask; // zero all non-important bits in data
	        b &= ~(mask); // zero all important bits in existing byte
	        b |= data; // combine data with existing byte
	        return I2CdevWriteByte(devAddr, regAddr, b);
	    } else {
	        return false;
	    }
	}
	/** Write multiple bits in a 16-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to write to
	 * @param bitStart First bit position to write (0-15)
	 * @param length Number of bits to write (not more than 16)
	 * @param data Right-aligned value to write
	 * @return Status of operation (true = success)
	 */
	boolean I2CdevWriteBitsW(int devAddr, int regAddr, int bitStart, int length, int data) {
	    //              010 value to write
	    // fedcba9876543210 bit numbers
	    //    xxx           args: bitStart=12, length=3
	    // 0001110000000000 mask word
	    // 1010111110010110 original value (sample)
	    // 1010001110010110 original & ~mask
	    // 1010101110010110 masked | value
	    int w = 0;
	    if (I2CdevReadWord(devAddr, regAddr, w) != 0) {
	        int mask = ((1 << length) - 1) << (bitStart - length + 1);
	        data <<= (bitStart - length + 1); // shift data into correct position
	        data &= mask; // zero all non-important bits in data
	        w &= ~(mask); // zero all important bits in existing word
	        w |= data; // combine data with existing word
	        return I2CdevWriteWord(devAddr, regAddr, w);
	    } else {
	        return false;
	    }
	}
	/** write a single bit in an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr Register regAddr to write to
	 * @param bitNum Bit position to write (0-7)
	 * @param value New bit value to write
	 * @return Status of operation (true = success)
	 */
	boolean I2CdevWriteBit(int devAddr, int regAddr, int bitNum, boolean data) {
	    int b = 0;
	    int newbyte = 0;
	    int bitmask = 1;
	    I2CdevReadByte(devAddr, regAddr, b);
	    // Set the specified bit to 1
	    if (data){
	    	newbyte = b | (bitmask << bitNum);
	    }
	    // Set the specified bit to 0
	    else {
	    	newbyte = (b & ~(bitmask << bitNum));
	    	}
	    return I2CdevWriteByte(devAddr, regAddr, newbyte);
	}
	boolean I2CdevWriteByte(int devAddr, int regAddr, int data) {
		int[] writebuffer = {data};
	    return I2CdevWriteBytes(devAddr, regAddr, 1, writebuffer);
	}
	
	boolean I2CdevWriteWord(int devAddr, int regAddr, int data) {
		int[] writebuffer = {data};
	    return I2CdevWriteWords(devAddr, regAddr, 1, writebuffer);
	}
	/** Write multiple bytes to an 8-bit device register.
	 * @param devAddr I2C slave device address
	 * @param regAddr First register address to write to
	 * @param length Number of bytes to write
	 * @param data Buffer to copy new data from
	 * @return Status of operation (true = success)
	 */
	boolean I2CdevWriteBytes(int devAddr, int regAddr, int length, int[] data) {
		byte[] writebuffer = new byte[length +1];
		writebuffer[0] = (byte)(regAddr & 0xff);
		for (int i=0 ; i<length ; i++){
			writebuffer[i+1] = (byte) (data[i] & 0xff); 
		}
		controller.i2cWrite(busAddress, devAddr, writebuffer, writebuffer.length);
		return true;
	}
	// TODO finish development
	boolean I2CdevWriteWords(int devAddr, int regAddr, int length, int[] data) {
		byte[] writebuffer = new byte[length*2 +1];
		writebuffer[0] = (byte)(regAddr & 0xff);
		for (int i=0 ; i<length ; i++){
			writebuffer[i*2+1] = (byte) (data[i] <<8); 		// MSByte
			writebuffer[i*2+2] = (byte) (data[i] & 0xff); 	// LSByte
		}
		controller.i2cWrite(busAddress, devAddr, writebuffer, writebuffer.length);
		return true;
	}
	
	/* 
	 * End of I2CDEV section. 
	 */
	
	/**
	 * This static method returns all the details of the class without it having
	 * to be constructed. It has description, categories, dependencies, and peer
	 * definitions.
	 * 
	 * @return ServiceType - returns all the data
	 * 
	 */
	static public ServiceType getMetaData() {

		ServiceType meta = new ServiceType(Mpu6050.class.getCanonicalName());
		meta.addDescription("General MPU 6050");
		meta.addCategory("microcontroller", "sensor");
		return meta;
	}

}
