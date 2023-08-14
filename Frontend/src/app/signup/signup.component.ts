import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { Router } from '@angular/router';
import { SnackbarService } from '../services/snackbar.service';
import { MatDialogRef } from '@angular/material/dialog';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { GlobalConstants } from '../shared/global-constants';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  password = true;
  confirmPassword = true;
  signUpForm: any = FormGroup;
  responseMessage: any;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private userService: UserService,
    private snackBarService: SnackbarService,
    private dialogRef: MatDialogRef<SignupComponent>,
    private ngxService: NgxUiLoaderService
  ) { }

  ngOnInit(): void {
    this.signUpForm = this.formBuilder.group({
      name:[null, [Validators.required, Validators.pattern(GlobalConstants.nameRegex)]],
      email:[null, [Validators.required, Validators.pattern(GlobalConstants.emailRegex)]],
      contactNumber:[null, [Validators.required, Validators.pattern(GlobalConstants.contactNumberRegex)]],
      password: [null, [Validators.required]],
      confirmPassword: [null, [Validators.required]],
    })
  }

  validateSubmit(){
    if(this.signUpForm.controls['password'].value != this.signUpForm.controls['confirmPassword'].value){
      return true;
    }else{
      return false;
    }
  }

 handleSubmit(){
  this.ngxService.start();
  var formData = this.signUpForm.value;
  var data ={
    name: formData.name,
    email: formData.email,
    contactNumber: formData.contactNumber,
    password: formData.password
  }
  this.userService.signup(data).subscribe((response: any) =>{
    this.ngxService.stop();
    this.dialogRef.close();
    this.responseMessage = response ?.message;
    this.snackBarService.openSnackBar(this.responseMessage, "");
    this.router.navigate(['/']);
  },(error)=>{
    this.ngxService.stop();
    if(error.error?.message){
      this.responseMessage = error.error?.message;
    }else{
      this.responseMessage = GlobalConstants.error;
    }
    this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
  })
}
}


