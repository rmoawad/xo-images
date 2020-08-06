
import { environment } from './../environments/environment';
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse, HttpResponse, HttpResponseBase } from '@angular/common/http';
import {  throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { UploadRequest } from './app-models/UploadRequest';
import { SearchCriteria } from './app-models/SearchCriteria';


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private API_SERVER_URL = environment.apiServerUrl;

  constructor(private httpClient: HttpClient) { }

  public saveImage(request:UploadRequest) {
    const uploadData = new FormData();
    uploadData.append('file', request.selectedImage);
    let params = new HttpParams();
    params = this.appendIfDefind(params, 'description', request.description);
    
    return this.httpClient
             .post(this.API_SERVER_URL + '/images', uploadData, {params: params})
             .pipe(catchError(this.handleError)); 
  }
  
  public search(searchCriteria:SearchCriteria) {  
    let params = new HttpParams();
    params = this.appendIfDefind(params, 'page', searchCriteria.page);
    params = this.appendIfDefind(params, 'description', searchCriteria.description);
    params = this.appendIfDefind(params, 'type', searchCriteria.type);
    params = this.appendIfDefind(params, 'size', searchCriteria.size);
    return this.httpClient
             .get(this.API_SERVER_URL + '/images', {params: params})
             .pipe(retry(3), catchError(this.handleError)); 
  }

  handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error!';
    if (error.error instanceof ErrorEvent) {
      // Client-side errors
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side errors
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.error.message || error.error}`;
    }
    return throwError(errorMessage);
  }

  private appendIfDefind(params:HttpParams, parameterName:string, value:any) {
    if (!!value) {
      params = params.append(parameterName, value);
    }
    return params;
  }
}
