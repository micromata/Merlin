import {getRestServiceUrl} from './global';

let dictionary;
let version;
let language;

const clearDictionary = () => {
    window.localStorage.removeItem('dictionary');
}

const loadDictionary = (version, language) => {

    const localDictionary = window.localStorage.getItem('dictionary');

    if (localDictionary) {
        const json = JSON.parse(localDictionary);

        if (json.version === version && json.language === language) {
            dictionary = json.dictionary;
            return;
        }
        //console.log("Version=" + version + ", lang="+ language + ", json.version=" + json.version + ", json.language=" + json.language);
    } else {
        //console.log("Version=" + version + ", lang="+ language + ", json=undefined");
    }
    fetchNewDictionary(version, language);
};

const fetchNewDictionary = (currentVersion, currentLanguage) => {
    //e.log(new Date().toISOString() + ": version=" + currentVersion + ", lang=" + currentLanguage);
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
            language = currentLanguage;
            saveDictionary();
        });
};

const saveDictionary = () => window.localStorage.setItem('dictionary', JSON.stringify({
    version, language, dictionary
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

export {clearDictionary, getTranslation, loadDictionary, fetchNewDictionary};
