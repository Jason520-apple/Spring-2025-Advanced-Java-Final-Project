// Travis Bauman, Amida Fombutu, Jason Vo
// OCCC Spring 2025
// Advanced Java
// Final Project - Person GUI Application



//our exception class will extend IllegalArgumentException rather than just plain Exception

//code below is from the exceptions demo code provided on Moodle

public class InvalidOCCCDateException extends IllegalArgumentException {

	
	private String msg = "Date entered is invalid!";
	
		public InvalidOCCCDateException() { //default constructor
			super();
			msg = "Date is invalid.";
		}
	
		public InvalidOCCCDateException(String msg) { //parameterized constructor
			super(msg);
			this.msg = msg;
		}
	
		public String getMessage() {
			return msg;
		}
	
		@Override
		public String toString() { //what happens when you call it
			return "InvalidOCCCDateException: " + msg;
		}
	
	
}

