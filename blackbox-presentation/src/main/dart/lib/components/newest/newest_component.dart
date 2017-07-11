import 'package:angular2/angular2.dart';
import 'dart:convert';
import 'dart:html';
import 'package:blackbox/models/message.dart';

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
    HttpRequest.request("../rest/messages",method: "GET",requestHeaders: {'Accept':'application/json'}).then((response){
      var json = JSON.decode(response.responseText);
      for(var i=0; i<json.length; i++)
      {
      	var msg = new Message();
      	var data = {};
      	if(json[i]["_original"] != undefined)
      		data = json[i]["_original"];
      	if(json[i]["_convert$_original"] != undefined)
      		data = json[i]["_convert$_original"];
      	
      	msg.content = data["content"];
      	msg.headline = data["headline"];
      	msg.id = data["id"];
      	msg.publishedOn = new DateTime.fromMillisecondsSinceEpoch(data["publishedOn"]);
      	messages.push(msg);      	
      }
      loaded = messages[0];
    }).catchError((n)=>print(n));
  }
}