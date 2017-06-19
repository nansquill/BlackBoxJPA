

import 'package:angular2/angular2.dart';
import 'dart:convert';
import 'dart:html';
import 'package:blackbox/model/message.dart';

@Component(selector: 'show-newest', templateUrl: 'newest_component.html')
class ShowNewest implements OnInit {

  Message loaded;

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
      var msg = new Message();
      msg.content = json["content"];
      msg.headline = json["headline"];
      msg.id = json["id"];
      msg.publishedOn = new DateTime.fromMillisecondsSinceEpoch(json["publishedOn"]);
      loaded = msg;
    }).catchError((n)=>print(n));
  }
}