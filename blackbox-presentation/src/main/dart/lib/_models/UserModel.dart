import 'dart:convert';

import 'package:angular2/angular2.dart';

class User {
	final String username;
	String password;

	Map<String, dynamic> toJson() => {
		'username' : JSON.encode(username),
		'password' : JSON.encode(password)
    };

    String toString() => '$username user';
	
	factory User.fromJson(Map<String, dynamic> usr) => 
		new Message(usr['username'], 
            usr['password']
        );	
	
	User(this.username, this.password);
}