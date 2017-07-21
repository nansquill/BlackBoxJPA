import 'dart:async';

import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import '../../models/Category.dart';
import '../../services/category/CategoryService.dart';

@Component(
	selector: 'categories',
	templateUrl: 'CategoryComponent.html',
	styleUrls: const ['CategoryComponent.css'],
	directives: const [CORE_DIRECTIVES, ROUTER_DIRECTIVES],
	providers: const [CategoryService, ROUTER_PROVIDERS]
)
class CategoryComponent implements OnInit {
	final CategoryService _catService;
	final Router _router;
	
	List<Category> categories;

	CategoryComponent(this._catService, this._router);
		
	Future<Null> ngOnInit() async {
		categories = (await _catService.getCategories()).toList();
	}
}