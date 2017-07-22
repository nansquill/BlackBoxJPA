import 'Category.dart';

class Message {
	final int id;
	String user;
	Category category;
	String headline;
	String content;	
	DateTime publishedOn;

	Map toJson() => {
		'id' : id,
		'user' : user,
		'category' : category,
		'headline' : headline,
		'content' : content,
		'published_on' : published_on
	};
	
	factory Message.fromJson(Map<String, dynamic> msg) => 
		new Message(_toInt(msg['id']), msg['user'], msg['category'], msg['headline'], msg['content'], msg['published_on']);
	
	Message(this.id, this.user, this.category, this.headline, this.content, this.publishedOn);
}

int _toInt(id) => id is int ? id : int.parse(id);