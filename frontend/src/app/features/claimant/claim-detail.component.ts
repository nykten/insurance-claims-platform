import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { StatusTimelineComponent } from '../../shared/ui/status-timeline.component';
import { Claim } from '../../api/claims.models';

@Component({selector:'app-claim-detail',standalone:true,imports:[DatePipe,StatusTimelineComponent],template:`@if (claim(); as item) {<article class="card"><p class="eyebrow">CLAIM DETAIL</p><h2>{{item.claimNumber}}</h2><p>{{item.description}}</p><div class="detail-grid"><span>Status<strong>{{item.status}}</strong></span><span>Type<strong>{{item.claimType}}</strong></span><span>Market<strong>{{item.market}}</strong></span><span>Amount<strong>{{item.claimedAmount | number:'1.2-2'}}</strong></span></div><app-status-timeline [events]="item.statusHistory ?? []"></app-status-timeline></article>}`})
export class ClaimDetailComponent { claim=input<Claim|null>(null); }
