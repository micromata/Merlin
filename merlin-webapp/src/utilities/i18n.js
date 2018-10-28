import {getRestServiceUrl} from './global';

let dictionary;
let version;

const loadDictionary = version => {

    const localDictionary = window.localStorage.getItem('dictionary');

    if (localDictionary) {
        const json = JSON.parse(localDictionary);

        if (json.version === version) {
            dictionary = json.dictionary;
            return;
        }
    }

    fetchNewDictionary(version);
};

const fetchNewDictionary = (currentVersion) => {
    fetch(getRestServiceUrl('i18n/list'), {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(response.statusText);
            }

            return response.json();
        })
        .then(json => {
            dictionary = json;
            version = currentVersion;
            saveDictionary();
        });
};

const saveDictionary = () => window.localStorage.setItem('dictionary', JSON.stringify({
    version, dictionary
}));

const getTranslation = (key, params) => {

    if (!dictionary) {
        return '';
    }

    let message = dictionary[key];

    if (message && params) {
        params.forEach((param, index) => {
            message = message.replace(`{${index}}`, param);
        });
    }

    return message;
};

export {getTranslation, loadDictionary, fetchNewDictionary};
