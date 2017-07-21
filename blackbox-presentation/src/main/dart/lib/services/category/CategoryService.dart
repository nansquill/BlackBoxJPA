import 'dart:async';

import 'package:angular2/angular2.dart';
import 'package:http/browser_client.dart' as httpBc;

import '../../models/Category.dart';
import '../../models/MockCategories.dart';

@Injectable()
class CategoryService extends httpBc.BrowserClient {
	Future<List<Category>> getCategories() async => MockCategories;
	
	Future<List<Category>> getCategoriesSlowly() {
		return new Future<List<Categories>>.delayed(
			const Duration(seconds: 2), getCategories
		);
	}
	
	Future<Category> getCategory(String name) async =>
		(await this.getCategories()).firstWhere((category) => category.name == name);
}