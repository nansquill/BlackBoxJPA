import 'dart:convert';

import 'package:angular2/angular2.dart';

class Category {
	final String name;

	Map<String, dynamic> toJson() => {
		'name' : JSON.encode(name)
    };

    String toString() => '$name category';
	
	factory Category.fromJson(Map<String, dynamic> cat) => 
		new Category(cat['name']);	
	
	Category(this.name);
}