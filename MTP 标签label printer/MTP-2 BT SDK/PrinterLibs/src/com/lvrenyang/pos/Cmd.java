package com.lvrenyang.pos;

public class Cmd {

	public static class PCmd {
		public static byte[] test = { 0x03, (byte) 0xFF, 0x20, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x08, 0x00, (byte) 0xD4, 0x18, 0x44, 0x45,
				0x56, 0x49, 0x43, 0x45, 0x3F, 0x3F };
		public static byte[] startUpdate = { 0x03, (byte) 0xFF, 0x2F, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xD3, 0x00 };
		// 更新程序里，下标4,5,6,7为参数偏移地址,10为包头校验和,11为数据校验和
		public static byte[] imaUpdate = { 0x03, (byte) 0xFF, 0x2E, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x01, (byte) 0xD3, 0x00 };
		public static byte[] endUpdate = { 0x03, (byte) 0xFF, 0x2F, 0x00,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x00, 0x00,
				(byte) 0xD3, 0x00 };

		// 更新字体
		public static byte[] fontUpdate = { 0x03, (byte) 0xFF, 0x2D, 0x00,
				0x00, 0x00, 0x00, 0x00, (byte) 0xFF, 0x00, 0x2E, 0x00 };

		public static byte[] setBaudrate = { 0x03, (byte) 0xFF, 0x2B, 0x00,
				(byte) 0x80, 0x25, 0x00, 0x00, 0x00, 0x00, 0x72, 0x00 };

		public static byte[] setPrintParam = { 0x03, (byte) 0xFF, 0x60, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x10, 0x00, (byte) 0x8C, 0x18,
				// 9600波特率
				(byte) 0x80, 0x25, 0x00, 0x00,

				// 语言
				(byte) 0xFF,

				// 加热浓度，0，1，2（2是最浓
				0x02,

				// 第6 byte 默认字体， 0--12x24 1--9x24 2--9x17 3--8x16 4--16x18
				0x00,

				// 第7 byte 换行命令： 0--0x0A 1--0x0D
				0x00,

				// 第8-9 byte空闲等待时间（单位：秒），高字节在后
				0x40, 0x00,
				// 第10-11 byte自动关机时间（单位：秒），高字节在后
				(byte) 0xFF, 0x00,

				// 第12-13 byte走纸键最大走纸长度（单位：毫米），高字节 在后
				(byte) 0xFF, 0x00,

				// 第14-15 byte黑标最大寻找距离（单位：毫米），高字节在 后
				(byte) 0xFF, 0x00 };

		public static byte[] readFlash = { 0x03, (byte) 0xFF, 0x2C, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xD0, 0x00 };

		public static byte[] setBluetooth = { 0x03, (byte) 0xFF, 0x61, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		public static byte[] setSystemInfo = { 0x03, (byte) 0xFF, 0x64, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	}

	// / <summary>
	// / 包含热敏打印机基本指令
	// / </summary>
	public static class ESCCmd {

		/**
		 * 设置密钥,一定是8个字节的密钥
		 */
		public static byte[] DES_SETKEY = { 0x1f, 0x1f, 0x00, 0x08, 0x00, 0x01,
				0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01 };

		/**
		 * 发送明文 0x00,0x00是明文数据长度。必须使用随机数。后面跟着的是明文数据 该命令会返回加密后的数据。相同格式。
		 */
		public static byte[] DES_ENCRYPT = { 0x1f, 0x1f, 0x01 };

		public static byte[] ERROR = { 0x00 };

		// / <summary>
		// / 复位打印机
		// / </summary>
		public static byte[] ESC_ALT = { 0x1b, 0x40 };

		// / <summary>
		// / 选择页模式
		// / </summary>
		public static byte[] ESC_L = { 0x1b, 0x4c };

		// / <summary>
		// / 页模式下取消打印数据
		// / </summary>
		public static byte[] ESC_CAN = { 0x18 };

		// / <summary>
		// / 打印并回到标准模式（在页模式下）
		// / </summary>
		public static byte[] FF = { 0x0c };

		// / <summary>
		// / 页模式下打印缓冲区所有内容
		// / 只在页模式下有效，不清除缓冲区内容
		// / </summary>
		public static byte[] ESC_FF = { 0x1b, 0x0c };

		// / <summary>
		// / 选择标准模式
		// / </summary>
		public static byte[] ESC_S = { 0x1b, 0x53 };

		// / <summary>
		// / 设置横向和纵向移动单位
		// / 分别将横向移动单位近似设置成1/x英寸，纵向移动单位设置成1/y英寸。
		// / 当x和y为0时，x和y被设置成默认值200。
		// / </summary>
		public static byte[] GS_P_x_y = { 0x1d, 0x50, 0x00, 0x00 };

		// / <summary>
		// / 选择国际字符集，值可以为0-15。默认值为0（美国）。
		// / </summary>
		public static byte[] ESC_R_n = { 0x1b, 0x52, 0x00 };

		// / <summary>
		// / 选择字符代码表，值可以为0-10,16-19。默认值为0。
		// / </summary>
		public static byte[] ESC_t_n = { 0x1b, 0x74, 0x00 };

		// / <summary>
		// / 打印并换行
		// / </summary>
		public static byte[] LF = { 0x0a };

		public static byte[] CR = { 0x0d };

		// / <summary>
		// / 设置行间距为[n*纵向或横向移动单位]英寸
		// / </summary>
		public static byte[] ESC_3_n = { 0x1b, 0x33, 0x00 };

		// / <summary>
		// / 设置字符右间距，当字符放大时，右间距也随之放大相同倍数
		// / </summary>
		public static byte[] ESC_SP_n = { 0x1b, 0x20, 0x00 };

		// / <summary>
		// / 在指定的钱箱插座引脚产生设定的开启脉冲。
		// / </summary>
		public static byte[] DLE_DC4_n_m_t = { 0x10, 0x14, 0x01, 0x00, 0x01 };

		// / <summary>
		// / 选择切纸模式并直接切纸，0为全切，1为半切
		// / </summary>
		public static byte[] GS_V_m = { 0x1d, 0x56, 0x00 };

		// / <summary>
		// / 进纸并且半切。
		// / </summary>
		public static byte[] GS_V_m_n = { 0x1d, 0x56, 0x42, 0x00 };

		// / <summary>
		// / 设置打印区域宽度，该命令仅在标准模式行首有效。
		// / 如果【左边距+打印区域宽度】超出可打印区域，则打印区域宽度为可打印区域-左边距。
		// / </summary>
		public static byte[] GS_W_nL_nH = { 0x1d, 0x57, 0x76, 0x02 };

		// / <summary>
		// / 设置绝对打印位置
		// / 将当前位置设置到距离行首（nL + nH x 256）处。
		// / 如果设置位置在指定打印区域外，该命令被忽略
		// / </summary>
		public static byte[] ESC_dollors_nL_nH = { 0x1b, 0x24, 0x00, 0x00 };

		/**
		 * 选择对齐方式 0 左对齐 1 中间对齐 2 右对齐
		 */
		public static byte[] ESC_a_n = { 0x1b, 0x61, 0x00 };

		// / <summary>
		// / 选择字符大小
		// / 0-3位选择字符高度，4-7位选择字符宽度
		// / 范围为从0-7
		// / </summary>
		public static byte[] GS_exclamationmark_n = { 0x1d, 0x21, 0x00 };

		// / <summary>
		// / 选择字体
		// / 0 标准ASCII字体
		// / 1 压缩ASCII字体
		// / </summary>
		public static byte[] ESC_M_n = { 0x1b, 0x4d, 0x00 };

		// / <summary>
		// / 选择/取消加粗模式
		// / n的最低位为0，取消加粗模式
		// / n最低位为1，选择加粗模式
		// / 与0x01即可
		// / </summary>
		public static byte[] GS_E_n = { 0x1b, 0x45, 0x00 };

		// / <summary>
		// / 选择/取消下划线模式
		// / 0 取消下划线模式
		// / 1 选择下划线模式（1点宽）
		// / 2 选择下划线模式（2点宽）
		// / </summary>
		public static byte[] ESC_line_n = { 0x1b, 0x2d, 0x00 };

		// / <summary>
		// / 选择/取消倒置打印模式
		// / 0 为取消倒置打印
		// / 1 选择倒置打印
		// / </summary>
		public static byte[] ESC_lbracket_n = { 0x1b, 0x7b, 0x00 };

		// / <summary>
		// / 选择/取消黑白反显打印模式
		// / n的最低位为0是，取消反显打印
		// / n的最低位为1时，选择反显打印
		// / </summary>
		public static byte[] GS_B_n = { 0x1d, 0x42, 0x00 };

		// / <summary>
		// / 选择/取消顺时针旋转90度
		// / </summary>
		public static byte[] ESC_V_n = { 0x1b, 0x56, 0x00 };

		// / <summary>
		// / 打印下载位图
		// / 0 正常
		// / 1 倍宽
		// / 2 倍高
		// / 3 倍宽、倍高
		// / </summary>
		public static byte[] GS_backslash_m = { 0x1d, 0x2f, 0x00 };

		// / <summary>
		// / 打印NV位图
		// / 以m指定的模式打印flash中图号为n的位图
		// / 1≤n≤255
		// / </summary>
		public static byte[] FS_p_n_m = { 0x1c, 0x70, 0x01, 0x00 };

		// / <summary>
		// / 选择HRI字符的打印位置
		// / 0 不打印
		// / 1 条码上方
		// / 2 条码下方
		// / 3 条码上、下方都打印
		// / </summary>
		public static byte[] GS_H_n = { 0x1d, 0x48, 0x00 };

		// / <summary>
		// / 选择HRI使用字体
		// / 0 标准ASCII字体
		// / 1 压缩ASCII字体
		// / </summary>
		public static byte[] GS_f_n = { 0x1d, 0x66, 0x00 };

		// / <summary>
		// / 选择条码高度
		// / 1≤n≤255
		// / 默认值 n=162
		// / </summary>
		public static byte[] GS_h_n = { 0x1d, 0x68, (byte) 0xa2 };

		// / <summary>
		// / 设置条码宽度
		// / 2≤n≤6
		// / 默认值 n=3
		// / </summary>
		public static byte[] GS_w_n = { 0x1d, 0x77, 0x03 };

		// / <summary>
		// / 打印条码
		// / 0x41≤m≤0x49
		// / n的取值有条码类型m决定
		// / </summary>
		public static byte[] GS_k_m_n_ = { 0x1d, 0x6b, 0x41, 0x0c };

		/**
		 * version: 1 <= v <= 17 error correction level: 1 <= r <= 4
		 */
		public static byte[] GS_k_m_v_r_nL_nH = { 0x1d, 0x6b, 0x61, 0x00, 0x02,
				0x00, 0x00 };

		// / <summary>
		// / 页模式下设置打印区域
		// / 该命令在标准模式下只设置内部标志位，不影响打印
		// / </summary>
		public static byte[] ESC_W_xL_xH_yL_yH_dxL_dxH_dyL_dyH = { 0x1b, 0x57,
				0x00, 0x00, 0x00, 0x00, 0x48, 0x02, (byte) 0xb0, 0x04 };

		// / <summary>
		// / 在页模式下选择打印区域方向
		// / 0≤n≤3
		// / </summary>
		public static byte[] ESC_T_n = { 0x1b, 0x54, 0x00 };

		// / <summary>
		// / 页模式下设置纵向绝对位置
		// / 这条命令只有在页模式下有效
		// / </summary>
		public static byte[] GS_dollors_nL_nH = { 0x1d, 0x24, 0x00, 0x00 };

		// / <summary>
		// / 页模式下设置纵向相对位置
		// / 页模式下，以当前点位参考点设置纵向移动距离
		// / 这条命令只在页模式下有效
		// / </summary>
		public static byte[] GS_backslash_nL_nH = { 0x1d, 0x5c, 0x00, 0x00 };

		// / <summary>
		// / 选择/取消汉字下划线模式
		// / </summary>
		public static byte[] FS_line_n = { 0x1c, 0x2d, 0x00 };

		/** 设置模块类型，缺省[7]=3, 0<=n<=16 */
		public static byte[] GS_leftbracket_k_pL_pH_cn_67_n = { 0x1d, 0x28,
				0x6b, 0x03, 0x00, 0x31, 0x43, 0x3 };

		/**
		 * 设置QR码的水平效验误差 缺省[7]=48, 48 - 7%, 49 - 15%, 50 - 25%, 51 - %30
		 */
		public static byte[] GS_leftbracket_k_pL_pH_cn_69_n = { 0x1d, 0x28,
				0x6b, 0x03, 0x00, 0x31, 0x45, 0x30 };

		/**
		 * 4<=pl+ph*256<=7092(0<=pl<=255,0<=ph<=28) （(pL + pH×256
		 * )-3）的字节在m(d1...dk)后作为图形的数据被处理。
		 */
		public static byte[] GS_leftbracket_k_pL_pH_cn_80_m__d1dk = { 0x1d,
				0x28, 0x6b, 0x03, 0x00, 0x31, 0x50, 0x30 };

		/**
		 * 打印二维码
		 */
		public static byte[] GS_leftbracket_k_pL_pH_cn_fn_m = { 0x1d, 0x28,
				0x6b, 0x03, 0x00, 0x31, 0x51, 0x30 };

	}

	public static class Constant {

		public static final int LAN_ADDR = 0x193000;

		// 第一段参数的地址
		public static final int LAN_INFO_ADDR = LAN_ADDR;
		public static final int LAN_INFO_LEN = 13;

		public static final int PRN_INFO_ADDR = LAN_INFO_ADDR + LAN_INFO_LEN;
		public static final int PRN_INFO_LEN = 40;

		public static final int OEM_NAME_LEN = 40;
		public static final int OEM_INFO_ADDR = (PRN_INFO_ADDR + PRN_INFO_LEN);
		public static final int OEM_INFO_LEN = (OEM_NAME_LEN * 4 + 2);

		public static final int USER_INFO_ADDR = (OEM_INFO_ADDR + OEM_INFO_LEN);
		public static final int USER_INFO_LEN = 50;

		public static final int BT_INFO_ADDR = (USER_INFO_ADDR + USER_INFO_LEN);
		public static final int BT_INFO_LEN = 30;

		public static final int IRD_INFO_ADDR = (BT_INFO_ADDR + BT_INFO_LEN);
		public static final int IRD_INFO_LEN = 30;

		public static final int FAC_INFO_ADDR = (IRD_INFO_ADDR + IRD_INFO_LEN);
		public static final int FAC_INFO_LEN = 68;

		public static final int USER_INFO2_ADDR = (FAC_INFO_ADDR + FAC_INFO_LEN);
		public static final int USER_INFO2_LEN = 20;
		// 最后一段参数

		public static final int PARA_LEN = (USER_INFO2_ADDR + USER_INFO2_LEN - LAN_INFO_ADDR);

		public static final int BT_MAX_NAME_LEN = 12;
		public static final int BT_MAX_PWD_LEN = 15;

		public static final int FAC_MAX_NAME_LEN = 32;
		public static final int FAC_MAX_SN_LEN = 29;

		public static final int BARCODE_TYPE_UPC_A = 0x41;
		public static final int BARCODE_TYPE_UPC_E = 0x42;
		public static final int BARCODE_TYPE_EAN13 = 0x43;
		public static final int BARCODE_TYPE_EAN8 = 0x44;
		public static final int BARCODE_TYPE_CODE39 = 0x45;
		public static final int BARCODE_TYPE_ITF = 0x46;
		public static final int BARCODE_TYPE_CODEBAR = 0x47;
		public static final int BARCODE_TYPE_CODE93 = 0x48;
		public static final int BARCODE_TYPE_CODE128 = 0x49;

		public static final int BARCODE_FONTPOSITION_NO = 0x00;
		public static final int BARCODE_FONTPOSITION_ABOVE = 0x01;
		public static final int BARCODE_FONTPOSITION_BELOW = 0x02;
		public static final int BARCODE_FONTPOSITION_ABOVEANDBELOW = 0x03;

		public static final int BARCODE_FONTTYPE_STANDARD = 0x00;
		public static final int BARCODE_FONTTYPE_SMALL = 0x01;

		public static final int ALIGN_LEFT = 0x00;
		public static final int ALIGN_CENTER = 0x01;
		public static final int ALIGN_RIGHT = 0x02;

		public static final int FONTSTYLE_NORMAL = 0x00;
		public static final int FONTSTYLE_BOLD = 0x08;
		public static final int FONTSTYLE_UNDERLINE1 = 0x80;
		public static final int FONTSTYLE_UNDERLINE2 = 0x100;
		public static final int FONTSTYLE_UPSIDEDOWN = 0x200;
		public static final int FONTSTYLE_BLACKWHITEREVERSE = 0x400;
		public static final int FONTSTYLE_TURNRIGHT90 = 0x1000;

		public static final int CODEPAGE_CHINESE = 255;
		public static final int CODEPAGE_BIG5 = 254;
		public static final int CODEPAGE_UTF_8 = 253;
		public static final int CODEPAGE_SHIFT_JIS = 252;
		public static final int CODEPAGE_EUC_KR = 251;
		public static final int CODEPAGE_CP437_Standard_Europe = 0;
		public static final int CODEPAGE_Katakana = 1;
		public static final int CODEPAGE_CP850_Multilingual = 2;
		public static final int CODEPAGE_CP860_Portuguese = 3;
		public static final int CODEPAGE_CP863_Canadian_French = 4;
		public static final int CODEPAGE_CP865_Nordic = 5;
		public static final int CODEPAGE_WCP1251_Cyrillic = 6;
		public static final int CODEPAGE_CP866_Cyrilliec = 7;
		public static final int CODEPAGE_MIK_Cyrillic_Bulgarian = 8;
		public static final int CODEPAGE_CP755_East_Europe_Latvian_2 = 9;
		public static final int CODEPAGE_Iran = 10;
		public static final int CODEPAGE_CP862_Hebrew = 15;
		public static final int CODEPAGE_WCP1252_Latin_I = 16;
		public static final int CODEPAGE_WCP1253_Greek = 17;
		public static final int CODEPAGE_CP852_Latina_2 = 18;
		public static final int CODEPAGE_CP858_Multilingual_Latin = 19;
		public static final int CODEPAGE_Iran_II = 20;
		public static final int CODEPAGE_Latvian = 21;
		public static final int CODEPAGE_CP864_Arabic = 22;
		public static final int CODEPAGE_ISO_8859_1_West_Europe = 23;
		public static final int CODEPAGE_CP737_Greek = 24;
		public static final int CODEPAGE_WCP1257_Baltic = 25;
		public static final int CODEPAGE_Thai = 26;
		public static final int CODEPAGE_CP720_Arabic = 27;
		public static final int CODEPAGE_CP855 = 28;
		public static final int CODEPAGE_CP857_Turkish = 29;
		public static final int CODEPAGE_WCP1250_Central_Eurpoe = 30;
		public static final int CODEPAGE_CP775 = 31;
		public static final int CODEPAGE_WCP1254_Turkish = 32;
		public static final int CODEPAGE_WCP1255_Hebrew = 33;
		public static final int CODEPAGE_WCP1256_Arabic = 34;
		public static final int CODEPAGE_WCP1258_Vietnam = 35;
		public static final int CODEPAGE_ISO_8859_2_Latin_2 = 36;
		public static final int CODEPAGE_ISO_8859_3_Latin_3 = 37;
		public static final int CODEPAGE_ISO_8859_4_Baltic = 38;
		public static final int CODEPAGE_ISO_8859_5_Cyrillic = 39;
		public static final int CODEPAGE_ISO_8859_6_Arabic = 40;
		public static final int CODEPAGE_ISO_8859_7_Greek = 41;
		public static final int CODEPAGE_ISO_8859_8_Hebrew = 42;
		public static final int CODEPAGE_ISO_8859_9_Turkish = 43;
		public static final int CODEPAGE_ISO_8859_15_Latin_3 = 44;
		public static final int CODEPAGE_Thai2 = 45;
		public static final int CODEPAGE_CP856 = 46;
		public static final int CODEPAGE_Cp874 = 47;

		public static final String[] strcodepages = { "CHINESE", "BIG5",
				"UTF-8", "SHIFT-JIS", "EUC-KR",
				"CP437 [U.S.A., Standard Europe]", "Katakana",
				"CP850 [Multilingual]", "CP860 [Portuguese]",
				"CP863 [Canadian-French]", "CP865 [Nordic]",
				"WCP1251 [Cyrillic]", "CP866 Cyrilliec #2",
				"MIK[Cyrillic /Bulgarian]", "CP755 [East Europe Latvian 2]",
				"Iran", "CP862 [Hebrew]", "WCP1252 Latin I", "WCP1253 [Greek]",
				"CP852 [Latina 2]", "CP858 Multilingual Latin)", "Iran II",
				"Latvian", "CP864 [Arabic]", "ISO-8859-1 [West Europe]",
				"CP737 [Greek]", "WCP1257 [Baltic]", "Thai", "CP720[Arabic]",
				"CP855", "CP857[Turkish]", "WCP1250[Central Eurpoe]", "CP775",
				"WCP1254[Turkish]", "WCP1255[Hebrew]", "WCP1256[Arabic]",
				"WCP1258[Vietnam]", "ISO-8859-2[Latin 2]",
				"ISO-8859-3[Latin 3]", "ISO-8859-4[Baltic]",
				"ISO-8859-5[Cyrillic]", "ISO-8859-6[Arabic]",
				"ISO-8859-7[Greek]", "ISO-8859-8[Hebrew]",
				"ISO-8859-9[Turkish]", "ISO-8859-15 [Latin 3]", "Thai2",
				"CP856", "Cp874" };

		public static final int[] ncodepages = { Constant.CODEPAGE_CHINESE,
				Constant.CODEPAGE_BIG5, Constant.CODEPAGE_UTF_8,
				Constant.CODEPAGE_SHIFT_JIS, Constant.CODEPAGE_EUC_KR,
				Constant.CODEPAGE_CP437_Standard_Europe,
				Constant.CODEPAGE_Katakana,
				Constant.CODEPAGE_CP850_Multilingual,
				Constant.CODEPAGE_CP860_Portuguese,
				Constant.CODEPAGE_CP863_Canadian_French,
				Constant.CODEPAGE_CP865_Nordic,
				Constant.CODEPAGE_WCP1251_Cyrillic,
				Constant.CODEPAGE_CP866_Cyrilliec,
				Constant.CODEPAGE_MIK_Cyrillic_Bulgarian,
				Constant.CODEPAGE_CP755_East_Europe_Latvian_2,
				Constant.CODEPAGE_Iran, Constant.CODEPAGE_CP862_Hebrew,
				Constant.CODEPAGE_WCP1252_Latin_I,
				Constant.CODEPAGE_WCP1253_Greek,
				Constant.CODEPAGE_CP852_Latina_2,
				Constant.CODEPAGE_CP858_Multilingual_Latin,
				Constant.CODEPAGE_Iran_II, Constant.CODEPAGE_Latvian,
				Constant.CODEPAGE_CP864_Arabic,
				Constant.CODEPAGE_ISO_8859_1_West_Europe,
				Constant.CODEPAGE_CP737_Greek,
				Constant.CODEPAGE_WCP1257_Baltic, Constant.CODEPAGE_Thai,
				Constant.CODEPAGE_CP720_Arabic, Constant.CODEPAGE_CP855,
				Constant.CODEPAGE_CP857_Turkish,
				Constant.CODEPAGE_WCP1250_Central_Eurpoe,
				Constant.CODEPAGE_CP775, Constant.CODEPAGE_WCP1254_Turkish,
				Constant.CODEPAGE_WCP1255_Hebrew,
				Constant.CODEPAGE_WCP1256_Arabic,
				Constant.CODEPAGE_WCP1258_Vietnam,
				Constant.CODEPAGE_ISO_8859_2_Latin_2,
				Constant.CODEPAGE_ISO_8859_3_Latin_3,
				Constant.CODEPAGE_ISO_8859_4_Baltic,
				Constant.CODEPAGE_ISO_8859_5_Cyrillic,
				Constant.CODEPAGE_ISO_8859_6_Arabic,
				Constant.CODEPAGE_ISO_8859_7_Greek,
				Constant.CODEPAGE_ISO_8859_8_Hebrew,
				Constant.CODEPAGE_ISO_8859_9_Turkish,
				Constant.CODEPAGE_ISO_8859_15_Latin_3, Constant.CODEPAGE_Thai2,
				Constant.CODEPAGE_CP856, Constant.CODEPAGE_Cp874 };

		public static String getCodePageStr(int nCodePage) {
			for (int i = 0; i < ncodepages.length; i++) {
				if (ncodepages[i] == nCodePage)
					return strcodepages[i];
			}
			return "";
		}

		public static int getCodePageInt(String strCodePage) {
			for (int i = 0; i < strcodepages.length; i++) {
				if (strcodepages[i].equals(strCodePage))
					return ncodepages[i];
			}
			return -1;
		}

		public static final int[] nbaudrate = { 4800, 9600, 19200, 38400,
				57600, 115200 };
		public static final String[] strbaudrate = { "4800", "9600", "19200",
				"38400", "57600", "115200", };

		public static String getBaudrateStr(int nBaudrate) {
			for (int i = 0; i < nbaudrate.length; i++) {
				if (nbaudrate[i] == nBaudrate)
					return strbaudrate[i];
			}
			return "";
		}

		public static int getBaudrateInt(String strBaudrate) {
			for (int i = 0; i < strbaudrate.length; i++) {
				if (strbaudrate[i].equals(strBaudrate))
					return nbaudrate[i];
			}
			return -1;
		}

		public static final int[] ndarkness = { 0, 1, 2 };
		public static final String[] strdarkness = { "Level 0", "Level 1",
				"Level 2" };

		public static String getDarknessStr(int nDarkness) {
			for (int i = 0; i < ndarkness.length; i++) {
				if (ndarkness[i] == nDarkness)
					return strdarkness[i];
			}
			return "";
		}

		public static int getDarknessInt(String strDarkness) {
			for (int i = 0; i < strdarkness.length; i++) {
				if (strdarkness[i].equals(strDarkness))
					return ndarkness[i];
			}
			return -1;
		}

		public static final int[] ndefaultfont = { 0, 1, 2, 3, 4 };
		public static final String[] strdefaultfont = { "12x24", "9x24",
				"9x17", "8x16", "16x18" };

		public static String getDefaultFontStr(int nDefaultFont) {
			for (int i = 0; i < ndefaultfont.length; i++) {
				if (ndefaultfont[i] == nDefaultFont)
					return strdefaultfont[i];
			}
			return "";
		}

		public static int getDefaultFontInt(String strDefaultFont) {
			for (int i = 0; i < strdefaultfont.length; i++) {
				if (strdefaultfont[i].equals(strDefaultFont))
					return ndefaultfont[i];
			}
			return -1;
		}

		public static final int[] nlinefeed = { 0, 1 };
		public static final String[] strlinefeed = { "LF", "CRLF" };

		public static String getLineFeedStr(int nLineFeed) {
			for (int i = 0; i < nlinefeed.length; i++) {
				if (nlinefeed[i] == nLineFeed)
					return strlinefeed[i];
			}
			return "";
		}

		public static int getLineFeedInt(String strLineFeed) {
			for (int i = 0; i < strlinefeed.length; i++) {
				if (strlinefeed[i].equals(strLineFeed))
					return nlinefeed[i];
			}
			return -1;
		}
	}

}
