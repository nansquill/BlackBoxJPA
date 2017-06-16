

import 'package:angular2/angular2.dart';
import 'dart:convert';
import 'dart:html';
import 'package:blackbox/model/messages.dart';

@Component(selector: 'show-newest', templateUrl: 'newest_component.html')
class ShowNewest implements OnInit {

  Messages loaded;

  @override
  ngOnInit() {
    fetchNewest();
  }

  void loadNewest(dynamic e){
    e.preventDefault();
    fetchNewest();
  }

  bool hasBeenLoaded() => loaded != null;

  void fetchNewest() {
    HttpRequest.request("../rest/messages/newest",method: "GET",requestHeaders: {'Accept':'application/json'}).then((response){
      var json = JSON.decode(response.responseText);
      var messages = new Messages();
      messages.content = json["content"];
      messages.headline = json["headline"];
      messages.id = json["id"];
      messages.publishedOn = new DateTime.fromMillisecondsSinceEpoch(json["publishedOn"]);
	  messages.isOnline = json["isOnline"];
      loaded = messages;
    }).catchError((n)=>print(n));
  }
}