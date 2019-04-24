package services;

import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import entity.AlgType;

public class AlgFactory {
	public static Object getAlgInstance(AlgType type) throws Exception{
		switch (type) {
		case AlgEucDisBase:
			return new AlgEucDisBase();
		case AlgEucDisFastRange:
			return new AlgEucDisFastRange();
		case AlgEucDisBaseOpticsWu:
			return new AlgEucDisBaseOpticsWu();
		case AlgEucDisAdvancedOpticsWu:
			return new AlgEucDisAdvancedOpticsWu();
		default:
			break;
		}
		return new AlgEucDisBase();
	}
}
