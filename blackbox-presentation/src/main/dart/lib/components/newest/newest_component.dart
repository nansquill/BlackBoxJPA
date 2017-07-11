import 'package:angular2/angular2.dart';
import 'dart:convert';
import 'dart:html';
import 'package:blackbox/models/message.dart';
import 'package:jsonx/jsonx.dart';

@Component(selector: 'show-newest', templateUrl: 'newest_component.html')
class ShowNewest implements OnInit {

  List<Message> messages;

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
    HttpRequest.request("../rest/messages/3",method: "GET",requestHeaders: {'Accept':'application/json'}).then((response){
      var json = decode(response.responseText);
      
      var n = new Message();
      msg.content = "Willkommen!";
      msg.headline = "Title";
      messages.push(msg);
      loaded = messages[0];
      
      var msg = new Message();
      msg.content = json["_original"]["content"];
      msg.headline = json["_original"]["headline"];
      msg.id = json["_original"]["id"];
      msg.publishedOn = new DateTime.fromMillisecondsSinceEpoch(json["_original"]["publishedOn"]);
      messages.push(msg);      
      
      loaded = messages[0];
    }).catchError((n)=>print(n));
  }
}