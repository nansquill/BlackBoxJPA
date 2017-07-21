import 'Category.dart';

class Message {
	final int id;
	String user;
	Category category;
	String headline;
	String content;	
	DateTime publishedOn;
	
	factory Message.fromJson(Map<String, dynamic> msg) => new Message(msg['id'], msg['user'], msg['category'], msg['headline'], msg['content'], msg['published_on']);
	
	Message(this.id, this.user, this.category, this.headline, this.content, this.publishedOn);
}