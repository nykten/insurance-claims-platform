import { TestBed } from '@angular/core/testing';
import { StatusTimelineComponent } from './status-timeline.component';

describe('StatusTimelineComponent', () => {
  it('renders every status event', async () => {
    const fixture = TestBed.createComponent(StatusTimelineComponent);
    fixture.componentRef.setInput('events', [{toStatus:'SUBMITTED'}, {toStatus:'UNDER_REVIEW', note:'Picked up'}]);
    await fixture.whenStable(); fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('li').length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('UNDER_REVIEW');
    expect(fixture.nativeElement.textContent).toContain('Picked up');
  });
});
