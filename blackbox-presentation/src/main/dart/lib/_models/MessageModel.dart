import 'dart:convert';

import 'package:angular2/angular2.dart';

import 'UserModel.dart';
import 'CategoryModel.dart';

class Message {
	final int id;
	User user;
	Category category;
	String headline;
	String content;	
	DateTime publishedOn;

	Map<String, dynamic> toJson() => {
		'id' : JSON.encode(id),
		'user' : user.toJson(),
		'category' : category.toJson(),
		'headline' : JSON.encode(headline),
		'content' : JSON.encode(content),
		'published_on' : JSON.encode(published_on.millisecondsSinceEpoch())
	};

    String toString() => '$headline: $content (created by ${user.toString()} in ${category.toString()} at $publishedOn)';
	
	factory Message.fromJson(Map<String, dynamic> msg) => 
		new Message(_toInt(msg['id']), 
            new User.fromJson(msg['user']), 
            new Category.fromJson(msg['category']), 
            msg['headline'], 
            msg['content'], 
            new DateTime(_toInt(msg['published_on']))
        );	
	
	Message(this.id, this.user, this.category, this.headline, this.content, this.publishedOn);
}

int _toInt(id) => id is int ? id : int.parse(id);