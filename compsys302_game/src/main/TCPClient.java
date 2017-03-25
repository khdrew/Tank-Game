package main;

//TCPClient.java


public class TCPClient{
	
	
	static String idString = "00000000";
	public static int idNo = 0;
	public static void newID(){
		idNo++;
		idString = String.valueOf(idNo);
		String append = "";
		for (int k = 0; k < (8 - idString.length()); k++){
			append = append + '0';
		}
		idString = append + idString;
	}
	
	public static void main (String[] args){
		System.out.println(idString);
		newID();
		System.out.println(idString);
		newID();
		newID();
		System.out.println(idString);
		System.out.println(idString);
	}
	
	
	
}