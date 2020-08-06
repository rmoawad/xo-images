import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ApiService } from '../api.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { SearchCriteria } from '../app-models/SearchCriteria';
import { Image } from '../app-models/Image';

@Component({
	selector: 'app-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.css']
})

export class SearchComponent implements OnInit, OnDestroy {

	images:Image[] = [];
	picker = null;
	searchCriteria = new SearchCriteria();
	loading:boolean;
	tableColumns:string[] = ['id', 'type', 'size', 'description'];
	page:number = 0;

	destroy$: Subject<boolean> = new Subject<boolean>();

	constructor(private apiService: ApiService, public dialog: MatDialog) { }

	public search(pageStep:number) {
		this.loading = true;
		this.images = [];
		this.page += pageStep;
		this.searchCriteria.page = this.page;
		this.apiService.search(this.searchCriteria)
			.pipe(takeUntil(this.destroy$))
			.subscribe((res: any) => {
				this.images = res.content;
			}).add(() => {
				this.loading = false;
		   });
	}

	ngOnInit() {
		this.search(0);
	}

	ngOnDestroy() { }
}
