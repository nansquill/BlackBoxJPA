import 'dart:async';
import 'dart:convert';
import 'dart:html';

import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';
import 'package:http/http.dart';


import '../../models/Message.dart';
import '../../services/message/MessageService.dart';


@Component(selector: 'create-message', templateUrl: 'CreateComponent.html')
class CreateComponent {
	final MessageService _msgService;
	final Router _router;
	
	Message model;
	
	CreateComponent(this._msgService, this._router);
	
	Future<Null> create() async
	{
		model = await this._msgService.create(model);
	}
	
	void postMessages(dynamic e)
	{
		e.preventDefault();
		var requestHeaders = {
			'Content-Type':'application/json',
			'Accept':'application/json'
		};
		HttpRequest.request("../rest/messages",method: "POST",sendData: model.toJSON(),requestHeaders: requestHeaders).catchError((n)=>print(n));
	}
}
