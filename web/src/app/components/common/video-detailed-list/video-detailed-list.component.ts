import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-video-detailed-list',
  templateUrl: './video-detailed-list.component.html',
  styleUrls: ['./video-detailed-list.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VideoDetailedListComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
