import { roleGuard } from './role.guard';

describe('roleGuard', () => {
  it('creates a route guard for the requested roles', () => {
    expect(roleGuard(['CLAIMANT'])).toEqual(jasmine.any(Function));
  });
});
