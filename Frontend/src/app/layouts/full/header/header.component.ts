import { Component } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ChangePasswordComponent } from 'src/app/material-component/dialog/change-password/change-password.component';
import { ConfigurationComponent } from 'src/app/material-component/dialog/configuration/configuration.component';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: []
})
export class AppHeaderComponent {

  role: any;
  constructor(private router: Router,
    private dialog: MatDialog) {
  }

  logout(){
    const dialog = new MatDialogConfig();
    dialog.data ={
      message:'Logout?',
      confirmation: true
    };
    const dialogRef = this.dialog.open(ConfigurationComponent, dialog);
    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe((response)=>{
      dialogRef.close();
      localStorage.clear();
      this.router.navigate(['/']);
    })
  }

  changePassword(){
    const dialog = new MatDialogConfig();
    dialog.width= "550px";
    const dialogRef = this.dialog.open(ChangePasswordComponent, dialog);
  }
}
