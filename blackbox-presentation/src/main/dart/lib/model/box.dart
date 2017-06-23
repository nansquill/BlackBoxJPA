import 'dart:convert';

class Box {
  String name;
  String description;
  DateTime createdOn;
  
  Box(this.name, this.description, this.createdOn);

  factory Box.fromJson(Map<String, dynamic> box) => new Box(box['name'], box['description'], box['created_on']);

  String toJSON() {
    return JSON.encode({
      'name': name,
      'description': description,
      'created_on': createdOn
    });
  }
}