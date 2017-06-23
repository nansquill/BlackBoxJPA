// Copyright (c) 2017. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'package:angular2/platform/browser.dart';
import 'package:angular2/angular2.dart';

import 'package:blackbox/app_component.dart';
import 'package:blackbox/components/box/box_service.dart';
import 'package:http/http.dart';

void main() {
  bootstrap(AppComponent, [provide(Client, useClass: BoxService)]);
}
