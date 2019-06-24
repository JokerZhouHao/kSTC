package services;

import algorithm.AlgEucDisAdvancedOpticsWu;
import algorithm.AlgEucDisBase;
import algorithm.AlgEucDisBaseOpticsWu;
import algorithm.AlgEucDisFastRange;
import algorithm.AlgInterface;
import entity.AlgType;
import entity.QueryParams;

public class AlgFactory {
	public static AlgInterface getAlgInstance(AlgType type, QueryParams qp) throws Exception{
		switch (type) {
		case AlgEucDisBase:
			return new AlgEucDisBase(qp);
		case AlgEucDisFastRange:
			return new AlgEucDisFastRange(qp);
		case AlgEucDisBaseOpticsWu:
			return new AlgEucDisBaseOpticsWu(qp);
		case AlgEucDisAdvancedOpticsWu:
			return new AlgEucDisAdvancedOpticsWu(qp);
		default:
			break;
		}
		return new AlgEucDisBase(qp);
	}
}
