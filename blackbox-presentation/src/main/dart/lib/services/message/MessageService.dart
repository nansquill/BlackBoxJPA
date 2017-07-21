import 'dart:async';

import 'package:angular2/angular2.dart';

import 'package:blackbox/models/Message.dart';
import 'package:blackbox/models/MockMessages.dart';

@Injectable()
class MessageService {
	Future<List<Message>> getMessages() async => MockMessages;
	
	Future<List<Message>> getMessagesSlowly() {
		return new Future<List<Message>>.delayed(
			const Duration(seconds: 2), getMessages
		);
	}
	
	Future<Message> getMessage(int id) async =>
		(await getMessages()).firstWhere((message) => message.id == id);
		
	Future<List<Message>> getMessageByCategory(Category category) async
	{
		List<Message> messages = await getMessages();
		List<Message> selected;
		
		messages.forEach((message) {
			if(message.category.name == category.name)
			{
				selected.add(message);
			}
		});
		
		return selected;
	}
}