import { bootstrapApplication } from '@angular/platform-browser';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: '<main><h1>CHX Claims Platform</h1><p>Frontend workspace is ready.</p></main>'
})
class AppComponent {}

bootstrapApplication(AppComponent).catch((error: unknown) => console.error(error));
