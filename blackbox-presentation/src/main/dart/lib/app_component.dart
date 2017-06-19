// Copyright (c) 2017. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'package:angular2/core.dart';
import 'package:angular2/angular2.dart';

import 'package:blackbox/components/newest/newest_component.dart';
import 'package:blackbox/components/create/create_component.dart';

@Component(
    selector: 'my-app',
    directives: const [ShowNewest,CreateMessages],
    template: '''
    <h1>Hello {{name}}</h1>
    <show-newest style="display: inline-block; vertical-align: top;"></show-newest>
    <create-message style="display: inline-block; vertical-align: top; margin-left: 5em;"></create-message>    
    ''')
class AppComponent {
  var name = 'Apppp';
}
