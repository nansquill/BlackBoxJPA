import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import 'package:blackbox/services/message/MessageService.dart';
import 'package:blackbox/components/message/MessageComponent.dart';
import 'package:blackbox/components/message/MessageDetailComponent.dart';
import 'package:blackbox/services/category/CategoryService.dart';
import 'package:blackbox/components/category/CategoryComponent.dart';


@Component(
	selector: 'blackbox',
	templateUrl: 'AppComponent.html',
	styleUrls: const['AppComponent.css'],
	directives: const [ROUTER_DIRECTIVES, MessageComponent, CategoryComponent],
	providers: const [MessageService, CategoryService, ROUTER_PROVIDERS])
@RouteConfig(const [
	const Route(path: '/messages', name: 'Messages', component: MessageComponent),
	const Route(path: '/categories', name: 'Categories', component: CategoryComponent, useAsDefault: true),
	const Route(path: '/messages/:id', name: 'MessageDetail', component: MessageDetailComponent)
])

class AppComponent	{
	String title = 'Schwarzes Brett';
}