package com.lvrenyang.utils;

public class IPString {

	public static byte[] IsIPValid(String ip)
	{
		byte[] ipbytes = new byte[4];
		int valid = 0;
		int s,e;
		String ipstr = ip + ".";
		s = 0;
		for(e = 0; e < ipstr.length(); e++)
		{
			if ('.' == ipstr.charAt(e))
			{
				if ((e - s > 3) || (e - s) <= 0)	// 最长3个字符
					return null;
				
				int ipbyte = -1;
				try{
					ipbyte = Integer.parseInt(ipstr.substring(s, e));
					if (ipbyte < 0 || ipbyte > 255)
						return null;
					else
						ipbytes[valid] = (byte) ipbyte;
				}
				catch(NumberFormatException exce)
				{
					return null;
				}
				s = e + 1;
				valid++;
			}
		}
		if (valid == 4)
			return ipbytes;
		else
			return null;
	}
	
}
