import reducers from './index';
import {loadConfig} from '../actions';

describe('reducers', () => {
    it('returns the initial state', () => {
        expect(
            reducers(undefined, {})
        ).toEqual({
            config: {}
        });
    });

    it('handles CONFIG_LOAD', () => {
        // TODO EXPECT ACTION
        fail('TODOs open');
    });

    it('handles CONFIG_RECEIVED', () => {
        const state = {
            config: {}
        };

        Object.freeze(state);

        expect(reducers(state, loadConfig()))
            .toEqual({
                config: {
                    // TODO ADD EXAMPLE CONFIG
                }
            });
        fail('TODOs open');
    });

    it('handles CONFIG_SET', () => {
        // TODO EXPECTS REST URL BE CALLED
        // TODO EXPECT THE STATE TO BE CHANGED
        fail('TODOs open');
    });
});
