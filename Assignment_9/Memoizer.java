import java.util.HashMap;

public class Memoizer {

	private Functor f;
	private HashMap hashMap;

	public Memoizer (Functor myf) {
		f = myf;
		hashMap = new HashMap();
	}

	public Object call(Object x) {
		if (hashMap.containsKey(x))
			return hashMap.get(x);
		Object val = f.fn(x);
		hashMap.put(x, val);
		return val;
	}
}