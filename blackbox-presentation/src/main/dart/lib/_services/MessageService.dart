import 'dart:async';
import 'dart:convert';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

import '../_models/MessageModel.dart';

@Injectable()
class MessageService{
	static const _messageUrl = 'rest/messages'; //URL to web api
	static final Map<String, String> _headers = {'Access-Control-Allow-Origin': '*','Content-Type': 'text/html'};
    final Client _http;

	MessageService(this._http);	
    
    Future<List<Message>> getAll() async {
        try {
        //return MockMessages;
        return await this._http.get(_messageUrl, _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Message> getById(number id) async {
        try {
        var url = '$_messageUrl/$id';
        return await this._http.get(url, _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Message> create(Message message) async {
        try {
        return await this._http.post(_messageUrl, JSON.encode(message.toJson()), _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Message> update(Message message) async {
        try {
        var id = message.id;
        var url = '$_messageUrl/$id';
        return await this._http.put(url, JSON.encode(message.toJson()), _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Message> delete(number id) async {
        try {
        var url = '$_messageUrl/$id';
        return await this._http.delete(url, _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }
     
    dynamic _handleException(dynamic ex) {
		print("MessageService");
		print(ex);  
	}
}