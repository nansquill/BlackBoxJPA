import 'dart:async';
import 'dart:convert';

import 'package:angular2/angular2.dart';
import 'package:http/http.dart';

import '../_models/CategoryModel.dart';

@Injectable()
class CategoryService{
	static const _categoryUrl = 'rest/types'; //URL to web api
	static final Map<String, String> _headers = {'Access-Control-Allow-Origin': '*','Content-Type': 'text/html'};
    final Client _http;

	CategoryService(this._http);	
    
    Future<List<Category>> getAll() async {
        try {
        //return MockCategories;
        return await this._http.get(_categoryUrl, _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }

    }

    Future<Category> getByName(String name) async {
        try {
        var url = '$_categoryUrl/$name';
        return await this._http.get(url, _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Category> create(Category category) async {
        try {return await this._http.post(_categoryUrl, JSON.encode(category.toJson()), _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Category> update(Category category) async {
        try {
        var name = category.name;
        var url = '$_categoryUrl/$name';
        return await this._http.put(url, JSON.encode(category.toJson()), _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    Future<Category> delete(String name) async {
        try {
        var url = '$_categoryUrl/$name';
        return await this._http.delete(url, _headers).map((Response response) => JSON.decode(response));
        }
        catch(ex) {
        _handleException(ex);
        }
    }

    dynamic _handleException(dynamic ex) {
		print("CategoryService");
		print(ex);  
	}
}