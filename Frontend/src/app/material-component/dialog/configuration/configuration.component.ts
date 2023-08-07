import { Component, EventEmitter, Inject, OnInit, inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.scss']
})
export class ConfigurationComponent implements OnInit {

  onEmitStatusChange = new EventEmitter();
  details: any={};

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData:any
  ) { }

  ngOnInit(): void {
    if(this.dialogData && this.dialogData.confirmation){
      this.details=this.dialogData
    }
  }

  handleChangeAction(){
    this.onEmitStatusChange.emit();
  }

}
