package me.hii488;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileIO {
	
	private static FileOutputStream fileOut;
	private static ObjectOutputStream out;
	
	public static void openSerialize(String path){
		File temp = new File(path);
		if(!temp.exists()){
			File temp2 = new File(path.substring(0, path.lastIndexOf("\\")));
			if(!temp2.exists()) temp2.mkdirs();
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			fileOut = new FileOutputStream(path);
			out = new ObjectOutputStream(fileOut);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void serialize(Object obj){
		try {
			out.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void endSerialize(){
		try {
			out.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Object deserialize(String path){
		try{
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Object result = in.readObject();
			in.close();
			fileIn.close();
			return result;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
}