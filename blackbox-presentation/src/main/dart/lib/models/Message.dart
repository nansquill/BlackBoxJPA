import 'Category.dart';

class Message {
	final int id;
	String headline;
	Category category;
	
	Message(this.id, this.headline, this.category);
}