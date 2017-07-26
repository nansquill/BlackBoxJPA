import 'dart:async';
import 'dart:convert';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

import '../_models/UserModel.dart';

@Injectable()
class UserService implements OnInit {
	static const _userUrl = 'rest/persons'; //URL to web api
    static const _loginUrl = 'login.jsp'; //URL to web api
    static const _logoutUrl = 'logout';
	static final Map<String, String> _headers = {'Access-Control-Allow-Origin': '*','Content-Type': 'text/html'};
    final Client _http;
    static User currentUser = null;

	UserService(this._http);

    Future<User> register(User user) async {
        //check if user already exist
        if(!(await this.getByUsername(user.username))) {
            print("[Info] User does not exist yet");
            return await this.create(user);
        }
        print("[Error] User already exist");
        return null;
    }

    Future<User> login(User user) async {
        //currently user logged in
        if(getUser())
        {
            print("[Error] User is already logged in");
            return logout();
        }
        //check if user already exist
        User result = await this.getByUsername(user.username);
        if(result) {
            print("[Info] User found in database");
            currentUser = result;
            await this._http.get(_loginUrl, JSON.encode(currentUser.toJson()), _headers);
        }
        return getUser();
    }

    Future<User> logout() async {
        User result = getUser();
        if(!result) {
            print("[Error] User is not logged in");
            return null;
        }
        print("[Info] User has been logged out");
        currentUser = null;
        await this._http.get(_logoutUrl, _headers);
        return result;        
    }

    User getUser() {
        return currentUser; 
    }
    
    Future<Null> ngOnInit() async {
    	currentUser = await auth();
    }	    
    	
	Future<User> auth() async {
		return await this._http.get(_userUrl, _headers).map((Response response) => JSON.decode(response));
	}    
    
    Future<List<User>> getAll() async {
        //return MockUsers;
        return await this._http.get(_userUrl, _headers).map((Response response) => JSON.decode(response));
    }

    Future<User> getByUsername(String username) async {
        var url = '$_userUrl/$username';
        return await this._http.get(url, _headers).map((Response response) => JSON.decode(response));
    }

    Future<User> create(User user) async {
        return await this._http.post(_userUrl, JSON.encode(user.toJson()), _headers).map((Response response) => JSON.decode(response));
    }

    Future<User> update(User user) async {
        var username = user.username;
        var url = '$_userUrl/$username';
        return await this._http.put(url, JSON.encode(user.toJson()), _headers).map((Response response) => JSON.decode(response));
    }

    Future<User> delete(String username) async {
        var url = '$_userUrl/$username';
        return await this._http.delete(url, _headers).map((Response response) => JSON.decode(response));
    }
}