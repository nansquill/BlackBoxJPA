import 'dart:async';
import 'dart:core';

import 'package:angular2/angular2.dart';
import 'package:angular2/router.dart';

import '../../models/Category.dart';
import '../../services/category/CategoryService.dart';
import '../../models/Message.dart';
import '../../services/message/MessageService.dart';

@Component(
	selector: 'messages', 
	templateUrl:'MessageComponent.html',
	styleUrls: const['MessageComponent.css'],
	directives: const [CORE_DIRECTIVES, ROUTER_DIRECTIVES],
	providers: const [CategoryService, MessageService, ROUTER_PROVIDERS],
	pipes: const[COMMON_PIPES]	
)
class MessageComponent implements OnInit {
	final MessageService _msgService;
	final CategoryService _catService;
	final Router _router;
	
	List<Category> categories;
	List<Message> messages;
	
	Category selectedCategory;
	String createCategory;
	Message selectedMessage;
	var createMessage;
	
	bool subscribeMe;
	String title = "Messages";

	MessageComponent(this._catService, this._msgService, this._router);	
	
	Future<Null> getMessages() async {
		if(subscribeMe)
		{
			messages = (await this._msgService.getOwnMessages()).toList();
		}
		else if(selectedCategory != null)
		{
			messages = (await this._msgService.getMessagesByCategory(selectedCategory.name)).toList();
		}
		else
		{
			messages = (await this._msgService.getMessages()).toList();
		}
	}
			
	Future<Null> getCategories() async {
		categories = (await _catService.getCategories()).toList();
	}
	
	Future<Null> ngOnInit() async {
		getCategories();
		subscribeMe = false;
		selectedCategory = null;
		selectedMessage = null;
		getMessages();
	}
	
	Future<Null> onSubscribeClick() async {
		getMessages();
	} 
	
	Future<Null> onCategoryChange(Category category) async{ 	
		selectedCategory = category; 
		selectedMessage = null; 
		getMessages();
	}
	
	void onSelect(Message message) { 
		selectedMessage = message;
		Category cat = selectedMessage.category;
		createCategory = cat.name;
	}

	
	Future<Null> save() async {
		Category cat = new Category(createCategory);
		dynamic et = {
			"id" : selectedMessage.id,
			"headline" : selectedMessage.headline,
			"content" : selectedMessage.content,
			"category" : cat,
			"user" : selectedMessage.user
		};
		await _msgService.update(et);//then goback
		selectedMessage = null;		
	}
	
	Future<Null> delete() async {
		await _msgService.delete(selectedMessage.id);
		messages.remove(selectedMessage);
		selectedMessage = null;
	}
	
	Future<Null> gotoDetail() => _router.navigate([
		'/MessageDetail',
		{'id': selectedMessage.id.toString()}
	]);
}