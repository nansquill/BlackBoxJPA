// Copyright (c) 2017. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'package:angular2/core.dart';
import 'package:angular2/angular2.dart';

import 'package:blackbox/components/newest/newest_component.dart';
import 'package:blackbox/components/create/create_component.dart';
import 'package:blackbox/components/register/register_user_component.dart';
import 'package:blackbox/components/showmessage/show_message.dart';

@Component(
    selector: 'my-app',
    directives: const [ShowNewest,CreateMessages,RegisterUser,ShowMessage],
    template: '''
    
    
    <h1>Hello {{name}}</h1>
    <show-newest style="display: inline-block; vertical-align: top;"></show-newest>
    <create-message style="display: inline-block; vertical-align: top; margin-left: 5em;"></create-message>
    <register-user style="display: inline-block; vertical-align: top; margin-left: 5em;"></register-user>    
    <h1>Nachrichen {{name}}</h1>
    <show-message style="backgroud: white; width: 100%; height: 900px"></show-message>
    
    ''')

    
class AppComponent {
  var name = 'Apppp3';
}
