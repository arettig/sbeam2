package sbeam2;

import java.util.HashMap;

public enum scale {
	ps(-12),
	ns(-9),
	μs(-6),
	ms(-3);
	private int value;
	private static HashMap<Integer, scale> map= new HashMap<Integer, scale>();
	static{
		for(scale s: scale.values()){
			map.put(s.value, s);
		}
	}
	
	private scale(int val){this.value = val;}
	public int value(){
		return value;
	}
	public static scale valueOf(int i){
		return map.get(i);
	}
	
	@Override
	public String toString(){
		return "" + this.value;
	}
}
