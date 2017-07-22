import 'dart:async';
import 'dart:convert';
import 'dart:html';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

@Component(selector: 'register', templateUrl: 'RegisterComponent.html')
class RegisterComponent {
	static const _registerUrl = '../rest/register';
	static final _headers = {'Content-Type': 'application/json'};
	final Client _http;
	
	RegisterComponent(this._http);
	
	String username;
	String password;
	String passwordCheck;
	
	bool registered = false;
	
	Future<Null> getRegister() async {
		try	{
			final response = await _http.post(_registerUrl, headers: _headers, body: JSON.encode({"username": username, "password" : password}));
			registered = true;
		}
		catch(e)
		{
			throw _handleError(e);
		}	
	}
	
	bool isRegistered() => registered;

	Exception _handleError(dynamic e)	{
		print(e);
		return new Exception('Server error; cause: $e');
	}
}
