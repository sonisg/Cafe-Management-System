import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { saveAs } from 'file-saver';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { BillService } from 'src/app/services/bill.service';
import { CategoryService } from 'src/app/services/category.service';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';
import { ViewBillProductsComponent } from '../dialog/view-bill-products/view-bill-products.component';
import { ConfigurationComponent } from '../dialog/configuration/configuration.component';

@Component({
  selector: 'app-view-bill',
  templateUrl: './view-bill.component.html',
  styleUrls: ['./view-bill.component.scss']
})
export class ViewBillComponent implements OnInit {

  displayedColumns: string[] = [
    'name',
    'email',
    'contactNumber',
    'paymentMethod',
    'total',
    'view'
  ];
  dataSource: any = [];
  responseMessage: any;

  
  constructor(private formBuilder: FormBuilder,
    private productService: ProductService,
    private ngxService: NgxUiLoaderService,
    private snackBarService: SnackbarService,
    private dialogConfig: MatDialogConfig,
    private router: Router,
    private dialog: MatDialog,
    private billService: BillService,
    private categoryService: CategoryService) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData();
  }
  tableData() {
    this.billService.getBills().subscribe((response:any)=>{
      this.ngxService.stop();
      this.dataSource = new MatTableDataSource(response);
    },(error: any) => {
      this.ngxService.stop();
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.error;
      }
      this.snackBarService.openSnackBar(
        this.responseMessage,
        GlobalConstants.error
      );
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  handleViewAction(values:any){
    const dialog = new MatDialogConfig();
    dialog.data={
      data: values
    };
    dialog.width ="850px";
    const dialogRef = this.dialog.open(ViewBillProductsComponent, dialog);
    this.router.events.subscribe(()=>{
      dialogRef.close();
    });
  }

  downloadReportAction(values:any){
    this.ngxService.start();
    var data={
      name: values.name,
      email:values.email,
      uuid: values.uuid,
      contactNumber: values.contactNumber,
      paymentMethod: values.paymentMethod,
      total: values.total.toString(),
      productDetails: values.productDetails
    }
   this.downloadFile(values.uuid, data);
   
  }

  downloadFile(fileName:string, data: any){
    this.billService.getPdf(data).subscribe((response:any)=>{
      saveAs(response, fileName+ '.pdf');
      this.ngxService.stop();
    })
  }

  handleDeleteAction(values:any){
    const dialog = new MatDialogConfig();
    dialog.data={
      message:'delete ' + values.name +' bill?',
      confirmation:true
    };
    dialog.width ="850px";
    const dialogRef = this.dialog.open(ConfigurationComponent, dialog);
    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe((response)=>{
      this.ngxService.start();
      this.deleteBill(values.id);
      dialogRef.close();
    })
  }

  deleteBill(id: any) {
    this.billService.delete(id).subscribe((response:any)=>{
      this.ngxService.stop();
      this.tableData();
      this.responseMessage=response?.message;
      this.snackBarService.openSnackBar(this.responseMessage,"success");
    },(error: any) => {
      this.ngxService.stop();
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.error;
      }
      this.snackBarService.openSnackBar(
        this.responseMessage,
        GlobalConstants.error
      );
    })
  }
}
