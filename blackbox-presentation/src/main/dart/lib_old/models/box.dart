import 'dart:convert';

class Box {
  String name;


  factory Box.fromJson(Map<String, dynamic> box) => new Box(box['name']);

  String toJSON() {
    return JSON.encode({
      'name': name
    });
  }
}