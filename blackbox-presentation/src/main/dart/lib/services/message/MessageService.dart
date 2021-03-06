import 'dart:async';
import 'dart:convert';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

import '../../models/Message.dart';
import '../../models/MockMessages.dart';

@Injectable()
class MessageService{
	static const _categoryUrl = '../rest/types'; 
	static const _messageUrl = '../rest/messages'; //URL to web api
	static final _headers = {'Content-Type': 'application/json', 'Accept':'application/json'};
	final Client _http;
	
	MessageService(this._http);
	
	Future<List<Message>> getMessages() async {
		try	{
			final response = await _http.get(_messageUrl);
			final messages = _extractData(response)
				.map((value) => new Message.fromJson(value))
				.toList();
			final lg = messages.length;
			print("[Info] Received $lg messages");
			return messages;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<Message> getMessage(int id) async {
		try	{
			final response = await _http.get('$_messageUrl/$id');
			Message msg = new Message.fromJson(_extractData(response));
			print("[Info] Received message $id");
			return msg;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
		
	Future<Message> update(dynamic message) async {
		try	{
			final url = '$_messageUrl/update';
			final response = 
				await _http.put(url, headers:_headers, body: JSON.encode(message));
			Message msg = new Message.fromJson(_extractData(response));
			final id = msg.id;
			print("[Info] Update message $id");
			return msg;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<Message> create(dynamic message) async {
		try {
			final response = await _http.post(_messageUrl, headers: _headers, body: JSON.encode(message));
			Message msg = new Message.fromJson(_extractData(response));
			final id = msg.id;
			print("[Info] Created message $id");
			return msg;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<Message> delete(int id) async {
		try	{
			final url = '$_messageUrl/delete/$id';
			await _http.delete(url, headers: _headers);
			print("[Info] Deleted message $id");
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<List<Message>> getMessagesByCategory(String name) async {
		try	{
			final url = '$_categoryUrl/$name/messages';
			final response = await _http.get(url);
			final messages = _extractData(response)
				.map((value) => new Message.fromJson(value))
				.toList();
			final lg = messages.length;
			print("[Info] Received $lg messages with category $name");
			return messages;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<List<Message>> getOwnMessages() async {
		try	{
			final url = '$_messageUrl/own';
			final response = await _http.get(url);
			final messages = _extractData(response)
				.map((value) => new Message.fromJson(value))
				.toList();
			final lg = messages.length;
			print("[Info] Received $lg own messages");
			return messages;
		}
		catch(e)	{
			throw _handleError(e);
		}
	}
	
	Future<List<Message>> getMessagesMock() async => MockMessages;
	
	dynamic _extractData(Response resp) => JSON.decode(resp.body);
	
	Exception _handleError(dynamic e)	{
		print("[Error] $e");
		return new Exception('Server error; cause: $e');
	}
}