package core;

public class RandomSeeding {

	private static int next = 0;
	private static int seedset = -1;
	private static int round = 1;
	
	public static void init(int s){
		if(s < 1) throw new SimError("seed set must be >= 1");
		RandomSeeding.seedset = s;
		RandomSeeding.next = 1;
		RandomSeeding.round = 1;
	}
	
	public static long seed(){
		if(RandomSeeding.next >= autoSeeds.length){
			RandomSeeding.next = 0;
			RandomSeeding.round++;
		}
		long val = autoSeeds[RandomSeeding.next++];
		long ret = val * RandomSeeding.seedset * RandomSeeding.round; 
		if(ret == 0) throw new SimError("seed calculation failed");
		//System.out.println("assigining seed " + ret + " to");
		//Thread.dumpStack();
		return ret;
	}
	
	// seed table taken from omnet, seeds with 
	// sufficient distance to prevent correlation
	private static final long autoSeeds[] = {
	    1, 1331238991, 1550655590, 930627303, 766698560, 372156336,
	    1645116277, 1635860990, 1154667137, 692982627, 1961833381,
	    713190994, 460575272, 1298018763, 1497719440, 2030952567,
	    901595110, 631930165, 354421844, 1213737427, 1800697079,
	    795809157, 821079954, 1624537871, 1918430133, 861482464,
	    1736896562, 1220028201, 634729389, 456922549, 23246132,
	    979545543, 1008653149, 1156436224, 1689665648, 1604778030,
	    1628735393, 1949577498, 550023088, 1726571906, 1267216792,
	    1750609806, 314116942, 736299588, 2095003524, 1281569003,
	    356484144, 1423591576, 2140958617, 1577658393, 1116852912,
	    1865734753, 1701937813, 301264580, 171817360, 1809513683,
	    360646631, 546534802, 1652205397, 136501886, 605448579,
	    1857604347, 1223969344, 668104522, 1821072732, 738721927,
	    1237280745, 1753702432, 2125855022, 1259255700, 935058038,
	    1325807218, 1151620124, 585222105, 1970906347, 1267057970,
	    66562942, 1959090863, 1503754591, 114059398, 2007872839,
	    1886985293, 1870986444, 2110445215, 1375265396, 1512926254,
	    470646700, 1951555990, 500432100, 1843528576, 347147950,
	    1431668171, 929595364, 1507452793, 800443847, 1428656866,
	    5715478, 1607979231, 2032092669, 37809413, 996425830,
	    1010869813, 1884232020, 312192738, 1821061493, 462270727,
	    248900140, 678804905, 905130946, 1892339752, 1307421505,
	    491642575, 1091346202, 1076664921, 1140141037, 122447008,
	    1244153851, 1382627577, 611793617, 1989326495, 808278095,
	    1352281487, 2106046913, 1731628906, 1226683843, 1683200486,
	    90274853, 1676390615, 2147466840, 498396356, 2140522509,
	    1217803227, 1146667727, 788324559, 1530171233, 317473611,
	    319146380, 992289339, 2077765218, 652681396, 789950931,
	    485020132, 632682054, 32775496, 1683083109, 603834907,
	    351704670, 1809710911, 171230418, 1511135464, 1986612391,
	    1646573708, 1411714374, 1546459273, 872179784, 1307370996,
	    801917373, 2051724276, 144283230, 1535180348, 1949917822,
	    650872229, 113201992, 890256110, 1965781805, 1903960687,
	    679060319, 452497769, 630187802, 174438105, 1298843779,
	    961082145, 1565131991, 2078229636, 50366922, 959177042,
	    144513213, 1423462005, 207666443, 152219823, 13354949,
	    412643566, 631135695, 166938633, 958408264, 1324624652,
	    494931978, 1472820641, 1150735880, 1508483704, 1640573652,
	    359288909, 1315013967, 1051019865, 1254156333, 1883764098,
	    587564032, 1288274932, 1912367727, 1595891993, 2138169990,
	    1794668172, 2059764593, 1152025509, 115613893, 926625010,
	    131630606, 706594585, 1386707532, 1624163092, 2081362360,
	    1882152641, 1428465736, 602313149, 1170668648, 863700348,
	    931140599, 1856765731, 197473249, 507314638, 1381732824,
	    252975355, 925311926, 1726193892, 576725369, 774762078,
	    198434005, 192355221, 1296038143, 1201667973, 653782169,
	    1426685702, 1503907840, 211726157, 33491376, 906578176,
	    238345926, 1826083853, 1366925216, 480315631, 1549695660,
	    1337366022, 1793656969, 1469954017, 1701980729, 98857548,
	    1883864564, 1709982325, 251608257, 1171967839, 642486710,
	    1358844649, 1115145546, 1398997376, 1021484058, 2035865982,
	};	
}