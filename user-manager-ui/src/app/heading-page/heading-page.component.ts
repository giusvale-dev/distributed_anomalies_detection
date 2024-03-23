import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-heading-page',
  templateUrl: './heading-page.component.html',
  styleUrls: ['./heading-page.component.css']
})
export class HeadingPageComponent {

    @Input() title: string
}
